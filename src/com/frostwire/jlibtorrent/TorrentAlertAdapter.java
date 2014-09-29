package com.frostwire.jlibtorrent;

import com.frostwire.jlibtorrent.alerts.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gubatron
 * @author aldenml
 */
public class TorrentAlertAdapter implements AlertListener {

    private static final Logger LOG = Logger.getLogger(TorrentAlertAdapter.class);

    protected final TorrentHandle th;

    private static final Map<String, CallAlertFunction> CALL_TABLE = buildCallAlertTable();

    public TorrentAlertAdapter(TorrentHandle th) {
        this.th = th;
    }

    @Override
    public boolean accept(Alert<?> alert) {
        if (!(alert instanceof TorrentAlert<?>)) {
            return false;
        }

        TorrentAlert<?> ta = (TorrentAlert<?>) alert;

        return ta.getSwig().getHandle().op_eq(th.getSwig());
    }

    @Override
    public void alert(Alert<?> alert) {
        CallAlertFunction function = CALL_TABLE.get(alert.getClass().getName());
        if (function != null) {
            function.invoke(this, alert);
        }
    }

    public void torrentAdded(TorrentAddedAlert alert) {
    }

    public void torrentFinished(TorrentFinishedAlert alert) {
    }

    public void torrentRemoved(TorrentRemovedAlert alert) {
    }

    public void torrentUpdate(TorrentUpdateAlert alert) {
    }

    public void blockFinished(BlockFinishedAlert alert) {
    }

    public void metadataReceived(MetadataReceivedAlert alert) {
    }

    public void metadataFailed(MetadataFailedAlert alert) {
    }

    public void saveResumeData(SaveResumeDataAlert alert) {
    }

    public void fileCompleted(FileCompletedAlert alert) {
    }

    public void fileRenamed(FileRenamedAlert alert) {
    }

    public void fileError(FileErrorAlert alert) {
    }

    public void trackerAnnounce(TrackerAnnounceAlert alert) {
    }

    public void readPiece(ReadPieceAlert alert) {
    }

    private static Map<String, CallAlertFunction> buildCallAlertTable() {
        Map<String, CallAlertFunction> map = new HashMap<String, CallAlertFunction>();

        for (Method m : TorrentAlertAdapter.class.getDeclaredMethods()) {
            Class<?> returnType = m.getReturnType();
            Class<?>[] parameterTypes = m.getParameterTypes();
            if (isAlertMethod(returnType, parameterTypes)) {
                try {
                    Class<?> clazz = parameterTypes[0];
                    CallAlertFunction function = new CallAlertFunction(m);

                    map.put(clazz.getName(), function);
                } catch (Throwable e) {
                    LOG.warn(e.toString());
                }
            }
        }

        return Collections.unmodifiableMap(map);
    }

    private static boolean isAlertMethod(Class<?> returnType, Class<?>[] parameterTypes) {
        return returnType.equals(void.class) && parameterTypes.length == 1 && Alert.class.isAssignableFrom(parameterTypes[0]);
    }

    private static final class CallAlertFunction {

        private final Method method;

        public CallAlertFunction(Method method) {
            this.method = method;
        }

        public void invoke(TorrentAlertAdapter adapter, Alert<?> alert) {
            try {
                method.invoke(adapter, alert);
            } catch (Throwable e) {
                LOG.warn(e.toString());
            }
        }
    }
}