/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.frostwire.jlibtorrent.swig;

public final class storage_mode_t {
  public final static storage_mode_t storage_mode_allocate = new storage_mode_t("storage_mode_allocate");
  public final static storage_mode_t storage_mode_sparse = new storage_mode_t("storage_mode_sparse");

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static storage_mode_t swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + storage_mode_t.class + " with value " + swigValue);
  }

  private storage_mode_t(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private storage_mode_t(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private storage_mode_t(String swigName, storage_mode_t swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static storage_mode_t[] swigValues = { storage_mode_allocate, storage_mode_sparse };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

