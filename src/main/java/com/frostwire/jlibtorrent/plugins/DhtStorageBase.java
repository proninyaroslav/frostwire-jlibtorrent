package com.frostwire.jlibtorrent.plugins;

import com.frostwire.jlibtorrent.Address;
import com.frostwire.jlibtorrent.DhtSettings;
import com.frostwire.jlibtorrent.Sha1Hash;
import com.frostwire.jlibtorrent.TcpEndpoint;
import com.frostwire.jlibtorrent.swig.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author gubatron
 * @author aldenml
 */
public final class DhtStorageBase implements DhtStorage {

    private final Sha1Hash id;
    private final DhtSettings settings;
    private final Counters counters;

    private final HashMap<String, TorrentEntry> map;

    public DhtStorageBase(Sha1Hash id, DhtSettings settings) {
        this.id = id;
        this.settings = settings;
        this.counters = new Counters();

        this.map = new HashMap<String, TorrentEntry>();
    }

    @Override
    public boolean getPeers(Sha1Hash infoHash, boolean noseed, boolean scrape, entry peers) {
        String hex = infoHash.toHex();
        TorrentEntry v = map.get(hex);

        if (v == null) {
            return false;
        }

        if (!v.name.isEmpty()) {
            peers.set("n", v.name);
        }

        if (scrape) {
            bloom_filter_256 downloaders = new bloom_filter_256();
            bloom_filter_256 seeds = new bloom_filter_256();

            for (PeerEntry peer : v.peers) {
                sha1_hash iphash = new sha1_hash();
                libtorrent.sha1_hash_address(peer.addr.address().swig(), iphash);
                if (peer.seed) {
                    seeds.set(iphash);
                } else {
                    downloaders.set(iphash);
                }
            }

            peers.set("BFpe", downloaders.to_bytes());
            peers.set("BFsd", seeds.to_bytes());
        } else {
            int num = Math.min(v.peers.size(), settings.maxPeersReply());
            Iterator<PeerEntry> iter = v.peers.iterator();
            entry_list pe = peers.get("values").list();
            byte_vector endpoint = new byte_vector();

            for (int t = 0, m = 0; m < num && iter.hasNext(); ++t) {
                PeerEntry e = iter.next();
                if ((Math.random() / (Integer.MAX_VALUE + 1.f)) * (num - t) >= num - m) continue;
                if (noseed && e.seed) continue;
                /*endpoint.resize(18);
                std::string::iterator out = endpoint.begin();
                write_endpoint(iter->addr, out);
                endpoint.resize(out - endpoint.begin());
                pe.push_back(entry(endpoint));*/

                ++m;
            }
        }

        return true;
    }

    @Override
    public void announcePeer(Sha1Hash infoHash, TcpEndpoint endp, String name, boolean seed) {

    }

    @Override
    public boolean getImmutableItem(Sha1Hash target, entry item) {
        return false;
    }

    @Override
    public void putImmutableItem(Sha1Hash target, byte[] buf, Address addr) {

    }

    @Override
    public long getMutableItemSeq(Sha1Hash target) {
        return 0;
    }

    @Override
    public boolean getMutableItem(Sha1Hash target, long seq, boolean forceFill, entry item) {
        return false;
    }

    @Override
    public void putMutableItem(Sha1Hash target, byte[] buf, byte[] sig, long seq, byte[] pk, byte[] salt, Address addr) {

    }

    @Override
    public void tick() {

    }

    @Override
    public Counters counters() {
        return counters;
    }

    static final class PeerEntry {

        public long added;
        public TcpEndpoint addr;
        public boolean seed;

        public static final Comparator<PeerEntry> COMPARATOR = new Comparator<PeerEntry>() {
            @Override
            public int compare(PeerEntry o1, PeerEntry o2) {
                TcpEndpoint a1 = o1.addr;
                TcpEndpoint a2 = o2.addr;

                int r = Address.compare(a1.address(), a2.address());

                return r == 0 ? Integer.compare(a1.port(), a2.port()) : r;
            }
        };
    }

    static final class TorrentEntry {
        public String name;
        public TreeSet<PeerEntry> peers;
    }
}