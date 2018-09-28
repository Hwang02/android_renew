// Copyright 2012 Square, Inc.

package com.savvi.rangedatepicker;

import java.util.Date;

/** Describes the state of a particular date cell in a {@link MonthView}. */
class MonthCellDescriptor {

  private final Date date;
  private final int value;
  private final boolean isCurrentMonth;
  private boolean isSelected;
  private final boolean isToday;
  private final boolean isSelectable;
  private boolean isHighlighted;
  private boolean isSun;
  private boolean isSat;


  public boolean isDeactivated() {
    return isDeactivated;
  }

  public void setDeactivated(boolean deactivated) {
    isDeactivated = deactivated;
  }

  private boolean isDeactivated;


  private boolean isUnavailable;
  private RangeState rangeState;

  MonthCellDescriptor(Date date, boolean currentMonth, boolean selectable, boolean selected,
      boolean today, boolean highlighted, int value, RangeState rangeState, boolean Sun, boolean Sat) {
    this.date = date;
    isCurrentMonth = currentMonth;
    isSelectable = selectable;
    isHighlighted = highlighted;
    isSelected = selected;
    isToday = today;
    this.isSun = Sun;
    this.isSat = Sat;
    this.value = value;
    this.rangeState = rangeState;
  }

  public Date getDate() {
    return date;
  }

  public boolean isCurrentMonth() {
    return isCurrentMonth;
  }

  public boolean isSelectable() {
    return isSelectable;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  public boolean isHighlighted() {
    return isHighlighted;
  }

  public void setHighlighted(boolean highlighted) {
    isHighlighted = highlighted;
  }

  public boolean isToday() {
    return isToday;
  }

  public boolean isSun() {
    return isSun;
  }

  public boolean isSat() {
    return isSat;
  }

  public RangeState getRangeState() {
    return rangeState;
  }

  public void setRangeState(RangeState rangeState) {
    this.rangeState = rangeState;
  }

  public int getValue() {
    return value;
  }


  public boolean isUnavailable() {
    return isUnavailable;
  }

  public void setUnavailable(boolean unavailable) {
    isUnavailable = unavailable;
  }

  @Override public String toString() {
    return "MonthCellDescriptor{"
        + "date="
        + date
        + ", value="
        + value
        + ", isCurrentMonth="
        + isCurrentMonth
        + ", isSelected="
        + isSelected
        + ", isToday="
        + isToday
        + ", isSelectable="
        + isSelectable
        + ", isHighlighted="
        + isHighlighted
        + ", rangeState="
        + rangeState
        + ", isDeactivated="
        + isDeactivated
        + ", isSun="
        + isSun
        + ", isSat="
        + isSat
            + '}';
  }
}
