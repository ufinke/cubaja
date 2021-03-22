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
 * <p>
 * Holiday definitions and informations.
 * </p>
 * <table class="striped">
 * <caption style="text-align:left">XML attributes and subelements</caption>
 * <thead>
 * <tr>
 * <th scope="col" style="text-align:left">Name</th>
 * <th scope="col" style="text-align:left">Description</th>
 * <th scope="col" style="text-align:center">A/E</th>
 * <th scope="col" style="text-align:center">M</th>
 * <th scope="col" style="text-align:center">U</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td style="text-align:left"><code>weekday</code></td>
 * <td style="text-align:left">defines weekdays as holiday (see {@link HolidayConfig.WeekdayConfig})</td>
 * <td style="text-align:center">E</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center"> </td>
 * </tr>
 * <tr>
 * <td style="text-align:left"><code>date</code></td>
 * <td style="text-align:left">defines a specific date as holiday (see {@link HolidayConfig.DateConfig})</td>
 * <td style="text-align:center">E</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center"> </td>
 * </tr>
 * <tr>
 * <td style="text-align:left"><code>fix</code></td>
 * <td style="text-align:left">defines a holiday by month and day (see {@link HolidayConfig.FixConfig})</td>
 * <td style="text-align:center">E</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center"> </td>
 * </tr>
 * <tr>
 * <td style="text-align:left"><code>easter</code></td>
 * <td style="text-align:left">defines easter dependent holiday (see {@link HolidayConfig.EasterConfig})</td>
 * <td style="text-align:center">E</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center"> </td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * <code>A/E</code>: attribute or subelement<br>
 * <code>M</code>: mandatory<br>
 * <code>U</code>: unique
 * </p>
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
   * <p>
   * Holiday configuration entry superclass.
   * Don't use this class directly; use subclasses.
   * </p><p>
   * The following XML attributes and subelements are common to all subclasses:
   * </p>
   * <table class="striped">
   * <caption style="text-align:left">XML attributes and subelements</caption>
   * <thead>
   * <tr>
   * <th scope="col" style="text-align:left">Name</th>
   * <th scope="col" style="text-align:left">Description</th>
   * <th scope="col" style="text-align:center">A/E</th>
   * <th scope="col" style="text-align:center">M</th>
   * <th scope="col" style="text-align:center">U</th>
   * </tr>
   * </thead>
   * <tbody>
   * <tr>
   * <td style="text-align:left"><code>name</code></td>
   * <td style="text-align:left">name of the holiday</td>
   * <td style="text-align:center">A</td>
   * <td style="text-align:center"> </td>
   * <td style="text-align:center">x</td>
   * </tr>
   * <tr>
   * <td style="text-align:left"><code>validFrom</code></td>
   * <td style="text-align:left">first day of period when this holiday is valid; format <code>yyyy-MM-dd</code></td>
   * <td style="text-align:center">A</td>
   * <td style="text-align:center"> </td>
   * <td style="text-align:center">x</td>
   * </tr>
   * <tr>
   * <td style="text-align:left"><code>validTo</code></td>
   * <td style="text-align:left">last day of period when this holiday is valid; format <code>yyyy-MM-dd</code></td>
   * <td style="text-align:center">A</td>
   * <td style="text-align:center"> </td>
   * <td style="text-align:center">x</td>
   * </tr>
   * </tbody>
   * </table>
   * <p>
   * <code>A/E</code>: attribute or subelement<br>
   * <code>M</code>: mandatory<br>
   * <code>U</code>: unique
   * </p>
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
     * @param name holiday name
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
     * @param validFrom first day of valid period
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
     * @param validTo last day of valid period
     */
    public void setValidTo(Date validTo) {

      this.validTo = validTo;
    }
  }

  // ----- date entry -----------------------------------------------------------------------------
  
  /**
   * <p>
   * Holiday configuration entry subclass for explicit dates.
   * </p><p>
   * Common XML attributes are described in {@link HolidayConfig.HolidayEntryConfig}.
   * </p>
   * <table class="striped">
   * <caption style="text-align:left">Special XML attributes and subelements</caption>
   * <thead>
   * <tr>
   * <th scope="col" style="text-align:left">Name</th>
   * <th scope="col" style="text-align:left">Description</th>
   * <th scope="col" style="text-align:center">A/E</th>
   * <th scope="col" style="text-align:center">M</th>
   * <th scope="col" style="text-align:center">U</th>
   * </tr>
   * </thead>
   * <tbody>
   * <tr>
   * <td style="text-align:left"><code>date</code></td>
   * <td style="text-align:left">date; format <code>yyyy-MM-dd</code></td>
   * <td style="text-align:center">A</td>
   * <td style="text-align:center"> </td>
   * <td style="text-align:center">x</td>
   * </tr>
   * </tbody>
   * </table>
   * <p>
   * <code>A/E</code>: attribute or subelement<br>
   * <code>M</code>: mandatory<br>
   * <code>U</code>: unique
   * </p>
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
     * The format is <code>yyyy-MM-dd</code>.
     * @param date single date
     */
    @Mandatory
    public void setDate(Date date) {

      this.date = date;
    }
  }

  // ----- weekday entry --------------------------------------------------------------------------
  
  /**
   * <p>
   * Holiday configuration entry subclass for weekdays.
   * </p><p>
   * Common XML attributes are described in {@link HolidayConfig.HolidayEntryConfig}.
   * </p>
   * <table class="striped">
   * <caption style="text-align:left">Special XML attributes and subelements</caption>
   * <thead>
   * <tr>
   * <th scope="col" style="text-align:left">Name</th>
   * <th scope="col" style="text-align:left">Description</th>
   * <th scope="col" style="text-align:center">A/E</th>
   * <th scope="col" style="text-align:center">M</th>
   * <th scope="col" style="text-align:center">U</th>
   * </tr>
   * </thead>
   * <tbody>
   * <tr>
   * <td style="text-align:left"><code>days</code></td>
   * <td style="text-align:left">comma separated list of {@link Weekday} constants</td>
   * <td style="text-align:center">A</td>
   * <td style="text-align:center"> </td>
   * <td style="text-align:center">x</td>
   * </tr>
   * </tbody>
   * </table>
   * <p>
   * <code>A/E</code>: attribute or subelement<br>
   * <code>M</code>: mandatory<br>
   * <code>U</code>: unique
   * </p>
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
     * @param days weekdays which are free days
     */
    @Mandatory
    public void setDays(Weekday[] days) {

      this.days = days;
    }
  }

  // ----- fix day entry --------------------------------------------------------------------------
  
  /**
   * <p>
   * Holiday configuration entry subclass for a yearly fixed holiday.
   * </p><p>
   * Common XML attributes are described in {@link HolidayConfig.HolidayEntryConfig}.
   * </p>
   * <table class="striped">
   * <caption style="text-align:left">Special XML attributes and subelements</caption>
   * <thead>
   * <tr>
   * <th scope="col" style="text-align:left">Name</th>
   * <th scope="col" style="text-align:left">Description</th>
   * <th scope="col" style="text-align:center">A/E</th>
   * <th scope="col" style="text-align:center">M</th>
   * <th scope="col" style="text-align:center">U</th>
   * </tr>
   * </thead>
   * <tbody>
   * <tr>
   * <td style="text-align:left"><code>month</code></td>
   * <td style="text-align:left">month number, ranging from <code>1</code> to <code>12</code></td>
   * <td style="text-align:center">A</td>
   * <td style="text-align:center"> </td>
   * <td style="text-align:center">x</td>
   * </tr>
   * <tr>
   * <td style="text-align:left"><code>day</code></td>
   * <td style="text-align:left">day of month</td>
   * <td style="text-align:center">A</td>
   * <td style="text-align:center"> </td>
   * <td style="text-align:center">x</td>
   * </tr>
   * </tbody>
   * </table>
   * <p>
   * <code>A/E</code>: attribute or subelement<br>
   * <code>M</code>: mandatory<br>
   * <code>U</code>: unique
   * </p>
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
     * @param day day of month
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
     * Sets the month in the range from <code>1</code> to <code>12</code>.
     * @param month month
     */
    @Mandatory
    public void setMonth(int month) {

      this.month = month;
    }

    /**
     * Checks correct settings.
     * Used internally during parse.
     * @throws ConfigException when the day / month combination is invalid
     */
    public void endElement() throws ConfigException {

      if (month < 1 || month > 12 || day < 1 || day > MAX_DAY[month]) {
        throw new ConfigException(text.get("fixHoliday", month, day)); 
      }
    }
  }

  // ----- easter entry ---------------------------------------------------------------------------
  
  /**
   * <p>
   * Holiday configuration entry subclass for a easter dependent holiday.
   * </p><p>
   * Common XML attributes are described in {@link HolidayConfig.HolidayEntryConfig}.
   * </p>
   * <table class="striped">
   * <caption style="text-align:left">Special XML attributes and subelements</caption>
   * <thead>
   * <tr>
   * <th scope="col" style="text-align:left">Name</th>
   * <th scope="col" style="text-align:left">Description</th>
   * <th scope="col" style="text-align:center">A/E</th>
   * <th scope="col" style="text-align:center">M</th>
   * <th scope="col" style="text-align:center">U</th>
   * </tr>
   * </thead>
   * <tbody>
   * <tr>
   * <td style="text-align:left"><code>offset</code></td>
   * <td style="text-align:left">number of days between easter sunday and this holiday</td>
   * <td style="text-align:center">A</td>
   * <td style="text-align:center"> </td>
   * <td style="text-align:center">x</td>
   * </tr>
   * </tbody>
   * </table>
   * <p>
   * <code>A/E</code>: attribute or subelement<br>
   * <code>M</code>: mandatory<br>
   * <code>U</code>: unique
   * </p>
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
     * @param offset day difference in comparision to easter sunday
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
   * @param cal calendar
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
   * @param date date
   * @return flag
   */
  public boolean isHoliday(Date date) {
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return isHoliday(cal);
  }
  
  /**
   * Shows if calendar value is a workday.
   * @param cal calendar
   * @return flag
   */
  public boolean isWorkday(Calendar cal) {
    
    return ! isHoliday(cal);
  }
  
  /**
   * Shows if date is a workday.
   * @param date date
   * @return flag
   */
  public boolean isWorkday(Date date) {
    
    return ! isHoliday(date);
  }
  
  /**
   * Adds a date entry.
   * @param dateConfig holiday definition for a single explicit date
   */
  public void addDate(DateConfig dateConfig) {
    
    entryList.add(dateConfig);
  }
  
  /**
   * Adds a weekday entry.
   * @param weekdayConfig holiday definition for weekdays
   */
  public void addWeekday(WeekdayConfig weekdayConfig) {
    
    entryList.add(weekdayConfig);
  }
  
  /**
   * Adds a fix day entry.
   * @param fixConfig holiday definition which occurs every year on the same day
   */
  public void addFix(FixConfig fixConfig) {
    
    entryList.add(fixConfig);
  }
  
  /**
   * Adds an easter dependent entry.
   * @param easterConfig holiday definition where the concrete date in a year depends on easter sunday
   */
  public void addEaster(EasterConfig easterConfig) {
    
    entryList.add(easterConfig);
  }
}