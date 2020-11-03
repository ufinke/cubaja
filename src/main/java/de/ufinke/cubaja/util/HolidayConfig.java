// Copyright (c) 2007 - 2012, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.config.EndElementHandler;
import de.ufinke.cubaja.config.Mandatory;

/**
 * Holiday definitions and informations.
 * <p>
 * XML attributes and subelements:
 * <blockquote>
 * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
 *   <tr bgcolor="#ccccff">
 *     <th align="left">Name</th>
 *     <th align="left">Description</th>
 *     <th align="center">A/E</th>
 *     <th align="center">M</th>
 *     <th align="center">U</th>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>weekday</tt></td>
 *     <td align="left" valign="top">defines weekdays as holiday (see {@link HolidayConfig.WeekdayConfig})</td>
 *     <td align="center" valign="top">E</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top"> </td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>date</tt></td>
 *     <td align="left" valign="top">defines a specific date as holiday (see {@link HolidayConfig.DateConfig})</td>
 *     <td align="center" valign="top">E</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top"> </td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>fix</tt></td>
 *     <td align="left" valign="top">defines a holiday by month and day (see {@link HolidayConfig.FixConfig})</td>
 *     <td align="center" valign="top">E</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top"> </td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>easter</tt></td>
 *     <td align="left" valign="top">defines easter dependent holiday (see {@link HolidayConfig.EasterConfig})</td>
 *     <td align="center" valign="top">E</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top"> </td>
 *     </tr>
 * </table>
 * <tt>A/E</tt>: attribute or subelement
 * <br>
 * <tt>M</tt>: mandatory
 * <br>
 * <tt>U</tt>: unique
 * </blockquote>
 * @author Uwe Finke
 */
public class HolidayConfig {

  static Text text = Text.getPackageInstance(HolidayConfig.class);
  
  // ----- HolidayCalendar ------------------------------------------------------------------------
  
  static class HolidayCalendar {

    private final List<HolidayEntryConfig> entryList;
    private Map<Integer, BitSet> map;
    
    HolidayCalendar(List<HolidayEntryConfig> entryList) {
      
      this.entryList = entryList;
      map = new ConcurrentHashMap<Integer, BitSet>();
    }
    
    boolean isHoliday(Calendar cal) {
      
      int year = cal.get(YEAR);
      BitSet set = map.get(year);
      if (set == null) {
        set = createSet(year);
        map.put(year, set);
      }
      return set.get(cal.get(DAY_OF_YEAR));
    }
    
    private BitSet createSet(int year) {
      
      BitSet set = new BitSet(367);
      Calendar cal = Calendar.getInstance();
      Calendar easter = null;
      
      for (HolidayConfig.HolidayEntryConfig entry : entryList) {
        switch (entry.getType()) {
          case DAY:
            addDate(cal, year, set, entry);
            break;
          case FIX:
            addFix(cal, year, set, entry);
            break;
          case WEEKDAY:
            addWeekday(cal, year, set, entry);
            break;
          case EASTER:
            if (easter == null) {
              easter = computeEaster(year);
            }
            addEaster(cal, easter, set, entry);
            break;
        }
      }
      
      return set;
    }
    
    private void addDate(Calendar cal, int year, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
      
      HolidayConfig.DateConfig config = (HolidayConfig.DateConfig) entry;
      
      cal.setTime(config.getDate());
      
      if (year != cal.get(YEAR)) {
        return;
      }
      if (! isValid(cal, entry)) {
        return;
      }
          
      set.set(cal.get(DAY_OF_YEAR));
    }
    
    private void addFix(Calendar cal, int year, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
      
      HolidayConfig.FixConfig config = (HolidayConfig.FixConfig) entry;
      
      cal.clear();
      cal.set(YEAR, year);
      cal.set(MONTH, config.getMonth() - 1);
      cal.set(DAY_OF_MONTH, config.getDay());
      
      if (! isValid(cal, entry)) {
        return;
      }
      
      set.set(cal.get(DAY_OF_YEAR));
    }
    
    private void addWeekday(Calendar cal, int year, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
      
      HolidayConfig.WeekdayConfig config = (HolidayConfig.WeekdayConfig) entry;    
      
      cal.clear();
      cal.set(YEAR, year);
      cal.set(MONTH, 0);
      cal.set(DAY_OF_MONTH, 1);
      if (entry.getValidFrom() != null) {
        if (Util.compare(entry.getValidFrom(), cal.getTime()) > 0) {
          cal.setTime(entry.getValidFrom());
        }
      }
      
      Calendar limit = Calendar.getInstance();
      limit.clear();
      limit.set(YEAR, year);
      limit.set(MONTH, 11);
      limit.set(DAY_OF_MONTH, 31);
      if (entry.getValidTo() != null) {
        if (Util.compare(entry.getValidTo(), limit.getTime()) < 0) {
          limit.setTime(entry.getValidTo());
        }
      }
      
      while (cal.compareTo(limit) <= 0) {
        int dayOfWeek = cal.get(DAY_OF_WEEK);
        for (Weekday weekday : config.getDays()) {
          if (weekday.getCalendarConstant() == dayOfWeek) {
            set.set(cal.get(DAY_OF_YEAR));
          }
        }
        cal.add(DATE, 1);
      }
    }
    
    private void addEaster(Calendar cal, Calendar easter, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
      
      HolidayConfig.EasterConfig config = (HolidayConfig.EasterConfig) entry;
      
      cal.clear();
      cal.set(YEAR, easter.get(YEAR));
      cal.set(MONTH, easter.get(MONTH));
      cal.set(DAY_OF_MONTH, easter.get(DAY_OF_MONTH));
      cal.add(DATE, config.getOffset());
      
      if (! isValid(cal, entry)) {
        return;
      }
      
      set.set(cal.get(DAY_OF_YEAR));
    }
    
    private boolean isValid(Calendar cal, HolidayConfig.HolidayEntryConfig entry) {
      
      Date date = null;
      
      Date from = entry.getValidFrom();
      if (from != null) {
        date = cal.getTime();
        if (Util.compare(from, date) > 0) {
          return false;
        }
      }
      
      Date to = entry.getValidTo();
      if (to != null) {
        if (date == null) {
          date = cal.getTime();
        }
        if (Util.compare(to, date) < 0) {
          return false;
        }
      }
      
      return true;
    }
    
    /**
     * Formula from Wikipedia (version Heiner Lichtenberg)
     * 
     *  1. die Saekularzahl:                                   K(X) = X div 100
     *  2. die saekulare Mondschaltung:                        M(K) = 15 + (3K + 3) div 4 - (8K + 13) div 25
     *  3. die saekulare Sonnenschaltung:                      S(K) = 2 - (3K + 3) div 4
     *  4. den Mondparameter:                                  A(X) = X mod 19
     *  5. den Keim für den ersten Vollmond im Fruehling:      D(A,M) = (19A + M) mod 30
     *  6. die kalendarische Korrekturgroesse:                 R(D,A) = (D + A div 11) div 29
     *  7. die Ostergrenze:                                    OG(D,R) = 21 + D - R
     *  8. den ersten Sonntag im Maerz:                        SZ(X,S) = 7 - (X + X div 4 + S) mod 7
     *  9. die Entfernung des Ostersonntags von der
     *     Ostergrenze (Osterentfernung in Tagen):             OE(OG,SZ) = 7 - (OG - SZ) mod 7
     * 10. das Datum des Ostersonntags als Maerzdatum
     *     (32. Maerz = 1. April usw.):                        OS = OG + OE
     *
     * @param x year
     * @return easter
     */
    private Calendar computeEaster(int x) {
      
      int k = x / 100;
      int m = 15 + (3 * k + 3) / 4 - (8 * k + 13) / 25;
      int s = 2 - (3 * k + 3) / 4;
      int a = x % 19;
      int d = (19 * a + m) % 30;
      int r = (d + a / 11) / 29;
      int og = 21 + d - r;
      int sz = 7 - (x + x / 4 + s) % 7;
      int oe = 7 - (og - sz) % 7;
      int os = og + oe;
      
      int month = 3;
      if (os > 31) {
        month = 4;
        os = os - 31;
      }
      
      return new Day(x, month, os);
    }
  }
  
  // ----- entry types ----------------------------------------------------------------------------
  
  static enum Type {

    DAY, WEEKDAY, FIX, EASTER
  }

  // ----- entry superclass -----------------------------------------------------------------------
  
  /**
   * Holiday configuration entry superclass.
   * Don't use this class directly; use subclasses.
   * <p>
   * The following XML attributes and subelements are common to all subclasses:
   * <blockquote>
   * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
   *   <tr bgcolor="#ccccff">
   *     <th align="left">Name</th>
   *     <th align="left">Description</th>
   *     <th align="center">A/E</th>
   *     <th align="center">M</th>
   *     <th align="center">U</th>
   *     </tr>
   *   <tr bgcolor="#eeeeff">
   *     <td align="left" valign="top"><tt>name</tt></td>
   *     <td align="left" valign="top">name of the holiday</td>
   *     <td align="center" valign="top">A</td>
   *     <td align="center" valign="top"> </td>
   *     <td align="center" valign="top">x</td>
   *     </tr>
   *   <tr bgcolor="#eeeeff">
   *     <td align="left" valign="top"><tt>validFrom</tt></td>
   *     <td align="left" valign="top">first day of period when this holiday is valid; format <tt>yyyy-MM-dd</tt></td>
   *     <td align="center" valign="top">A</td>
   *     <td align="center" valign="top"> </td>
   *     <td align="center" valign="top">x</td>
   *     </tr>
   *   <tr bgcolor="#eeeeff">
   *     <td align="left" valign="top"><tt>validTo</tt></td>
   *     <td align="left" valign="top">last day of period when this holiday is valid; format <tt>yyyy-MM-dd</tt></td>
   *     <td align="center" valign="top">A</td>
   *     <td align="center" valign="top"> </td>
   *     <td align="center" valign="top">x</td>
   *     </tr>
   * </table>
   * <tt>A/E</tt>: attribute or subelement
   * <br>
   * <tt>M</tt>: mandatory
   * <br>
   * <tt>U</tt>: unique
   * </blockquote>
   */
  static protected class HolidayEntryConfig {

    private Type type;
    private String name;
    private Date validFrom;
    private Date validTo;

    HolidayEntryConfig(Type type) {

      this.type = type;
    }

    Type getType() {

      return type;
    }

    /**
     * Sets the name of the holiday.
     * The name is not used by the implementation.
     * The only purpose of this attribute is documentation in the configuration file.
     * @param name
     */
    public void setName(String name) {

      this.name = name;
    }

    /**
     * Returns the holidays name.
     * @return name
     */
    public String getName() {

      if (name == null) {
        name = "";
      }
      return name;
    }

    /**
     * Returns the first day of period when this holiday is valid.
     * @return date
     */
    public Date getValidFrom() {

      return validFrom;
    }

    /**
     * Sets the first day of period when this holiday is valid.
     * By default, there is no limitation.
     * @param validFrom
     */
    public void setValidFrom(Date validFrom) {

      this.validFrom = validFrom;
    }

    /**
     * Returns the last day of period when this holiday is velid.
     * @return date
     */
    public Date getValidTo() {

      return validTo;
    }

    /**
     * Sets the last day of period when this holiday is valid.
     * By default, there is no limitation.
     * @param validTo
     */
    public void setValidTo(Date validTo) {

      this.validTo = validTo;
    }
  }

  // ----- date entry -----------------------------------------------------------------------------
  
  /**
   * Holiday configuration entry subclass for explicit dates.
   * <p>
   * Common XML attributes are described in {@link HolidayConfig.HolidayEntryConfig}.
   * Special XML attributes and subelements:
   * <blockquote>
   * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
   *   <tr bgcolor="#ccccff">
   *     <th align="left">Name</th>
   *     <th align="left">Description</th>
   *     <th align="center">A/E</th>
   *     <th align="center">M</th>
   *     <th align="center">U</th>
   *     </tr>
   *   <tr bgcolor="#eeeeff">
   *     <td align="left" valign="top"><tt>date</tt></td>
   *     <td align="left" valign="top">date; format <tt>yyyy-MM-dd</tt></td>
   *     <td align="center" valign="top">A</td>
   *     <td align="center" valign="top"> </td>
   *     <td align="center" valign="top">x</td>
   *     </tr>
   * </table>
   * <tt>A/E</tt>: attribute or subelement
   * <br>
   * <tt>M</tt>: mandatory
   * <br>
   * <tt>U</tt>: unique
   * </blockquote>
   */
  static public class DateConfig extends HolidayEntryConfig {

    private Date date;

    /**
     * Constructor.
     */
    public DateConfig() {

      super(Type.DAY);
    }

    /**
     * Returns the date.
     * @return date
     */
    public Date getDate() {

      return date;
    }

    /**
     * Sets the date.
     * The format is <tt>yyyy-MM-dd</tt>.
     * @param date
     */
    @Mandatory
    public void setDate(Date date) {

      this.date = date;
    }
  }

  // ----- weekday entry --------------------------------------------------------------------------
  
  /**
   * Holiday configuration entry subclass for weekdays.
   * <p>
   * Common XML attributes are described in {@link HolidayConfig.HolidayEntryConfig}.
   * Special XML attributes and subelements:
   * <blockquote>
   * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
   *   <tr bgcolor="#ccccff">
   *     <th align="left">Name</th>
   *     <th align="left">Description</th>
   *     <th align="center">A/E</th>
   *     <th align="center">M</th>
   *     <th align="center">U</th>
   *     </tr>
   *   <tr bgcolor="#eeeeff">
   *     <td align="left" valign="top"><tt>days</tt></td>
   *     <td align="left" valign="top">comma separated list of {@link Weekday} constants</td>
   *     <td align="center" valign="top">A</td>
   *     <td align="center" valign="top"> </td>
   *     <td align="center" valign="top">x</td>
   *     </tr>
   * </table>
   * <tt>A/E</tt>: attribute or subelement
   * <br>
   * <tt>M</tt>: mandatory
   * <br>
   * <tt>U</tt>: unique
   * </blockquote>
   */
  static public class WeekdayConfig extends HolidayEntryConfig {

    private Weekday[] days;

    /**
     * Constructor.
     */
    public WeekdayConfig() {

      super(Type.WEEKDAY);
    }

    /**
     * Returns the days.
     * @return days
     */
    public Weekday[] getDays() {

      return days;
    }

    /**
     * Sets the days.
     * @param days
     */
    @Mandatory
    public void setDays(Weekday[] days) {

      this.days = days;
    }
  }

  // ----- fix day entry --------------------------------------------------------------------------
  
  /**
   * Holiday configuration entry subclass for a yearly fixed holiday.
   * <p>
   * Common XML attributes are described in {@link HolidayConfig.HolidayEntryConfig}.
   * Special XML attributes and subelements:
   * <blockquote>
   * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
   *   <tr bgcolor="#ccccff">
   *     <th align="left">Name</th>
   *     <th align="left">Description</th>
   *     <th align="center">A/E</th>
   *     <th align="center">M</th>
   *     <th align="center">U</th>
   *     </tr>
   *   <tr bgcolor="#eeeeff">
   *     <td align="left" valign="top"><tt>month</tt></td>
   *     <td align="left" valign="top">month number, ranging from <tt>1</tt> to <tt>12</tt></td>
   *     <td align="center" valign="top">A</td>
   *     <td align="center" valign="top"> </td>
   *     <td align="center" valign="top">x</td>
   *     </tr>
   *   <tr bgcolor="#eeeeff">
   *     <td align="left" valign="top"><tt>day</tt></td>
   *     <td align="left" valign="top">day of month</td>
   *     <td align="center" valign="top">A</td>
   *     <td align="center" valign="top"> </td>
   *     <td align="center" valign="top">x</td>
   *     </tr>
   * </table>
   * <tt>A/E</tt>: attribute or subelement
   * <br>
   * <tt>M</tt>: mandatory
   * <br>
   * <tt>U</tt>: unique
   * </blockquote>
   */
  static public class FixConfig extends HolidayEntryConfig implements EndElementHandler {

    static private int[] MAX_DAY = new int[] {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    
    private int day;
    private int month;

    /**
     * Constructor.
     */
    public FixConfig() {

      super(Type.FIX);
    }

    /**
     * Returns the day.
     * @return day
     */
    public int getDay() {

      return day;
    }

    /**
     * Sets the day.
     * @param day
     */
    @Mandatory
    public void setDay(int day) {

      this.day = day;
    }

    /**
     * Returns the month.
     * @return month
     */
    public int getMonth() {

      return month;
    }

    /**
     * Sets the month in the range from <tt>1</tt> to <tt>12</tt>.
     * @param month
     */
    @Mandatory
    public void setMonth(int month) {

      this.month = month;
    }

    /**
     * Checks correct settings.
     * Used internally during parse.
     * @throws ConfigException
     */
    public void endElement() throws ConfigException {

      if (month < 1 || month > 12 || day < 1 || day > MAX_DAY[month]) {
        throw new ConfigException(text.get("fixHoliday", month, day)); 
      }
    }
  }

  // ----- easter entry ---------------------------------------------------------------------------
  
  /**
   * Holiday configuration entry subclass for a easter dependent holiday.
   * <p>
   * Common XML attributes are described in {@link HolidayConfig.HolidayEntryConfig}.
   * Special XML attributes and subelements:
   * <blockquote>
   * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
   *   <tr bgcolor="#ccccff">
   *     <th align="left">Name</th>
   *     <th align="left">Description</th>
   *     <th align="center">A/E</th>
   *     <th align="center">M</th>
   *     <th align="center">U</th>
   *     </tr>
   *   <tr bgcolor="#eeeeff">
   *     <td align="left" valign="top"><tt>offset</tt></td>
   *     <td align="left" valign="top">number of days between easter sunday and this holiday</td>
   *     <td align="center" valign="top">A</td>
   *     <td align="center" valign="top"> </td>
   *     <td align="center" valign="top">x</td>
   *     </tr>
   * </table>
   * <tt>A/E</tt>: attribute or subelement
   * <br>
   * <tt>M</tt>: mandatory
   * <br>
   * <tt>U</tt>: unique
   * </blockquote>
   */
  static public class EasterConfig extends HolidayEntryConfig {

    private int offset;

    /**
     * Constructor.
     */
    public EasterConfig() {

      super(Type.EASTER);
    }

    /**
     * Returns the offset.
     * @return offset
     */
    public int getOffset() {

      return offset;
    }

    /**
     * Sets the offset to easter sunday.
     * Negative values define days before easter.
     * @param offset
     */
    @Mandatory
    public void setOffset(int offset) {

      this.offset = offset;
    }
  }
  
  // ----- HolidayConfig --------------------------------------------------------------------------
  
  private List<HolidayEntryConfig> entryList;
  private HolidayCalendar holidayCalendar;
  
  /**
   * Constructor.
   */
  public HolidayConfig() {
  
    entryList = new ArrayList<HolidayEntryConfig>();
  }
  
  /**
   * Shows if calendar value is a holiday.
   * @param cal
   * @return flag
   */
  public boolean isHoliday(Calendar cal) {
    
    if (holidayCalendar == null) {
      holidayCalendar = new HolidayCalendar(entryList);
    }
    return holidayCalendar.isHoliday(cal);
  }
  
  /**
   * Shows if date is a holiday.
   * @param date
   * @return flag
   */
  public boolean isHoliday(Date date) {
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return isHoliday(cal);
  }
  
  /**
   * Shows if calendar value is a workday.
   * @param cal
   * @return flag
   */
  public boolean isWorkday(Calendar cal) {
    
    return ! isHoliday(cal);
  }
  
  /**
   * Shows if date is a workday.
   * @param date
   * @return flag
   */
  public boolean isWorkday(Date date) {
    
    return ! isHoliday(date);
  }
  
  /**
   * Adds a date entry.
   * @param dateConfig
   */
  public void addDate(DateConfig dateConfig) {
    
    entryList.add(dateConfig);
  }
  
  /**
   * Adds a weekday entry.
   * @param weekdayConfig
   */
  public void addWeekday(WeekdayConfig weekdayConfig) {
    
    entryList.add(weekdayConfig);
  }
  
  /**
   * Adds a fix day entry.
   * @param fixConfig
   */
  public void addFix(FixConfig fixConfig) {
    
    entryList.add(fixConfig);
  }
  
  /**
   * Adds an easter dependent entry.
   * @param easterConfig
   */
  public void addEaster(EasterConfig easterConfig) {
    
    entryList.add(easterConfig);
  }
}