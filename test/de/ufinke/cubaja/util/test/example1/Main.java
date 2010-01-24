package de.ufinke.cubaja.util.test.example1;

import java.util.Date;
import de.ufinke.cubaja.config.Configurator;
import de.ufinke.cubaja.util.Application;
import de.ufinke.cubaja.util.Day;
import de.ufinke.cubaja.util.HolidayCalendar;
import de.ufinke.cubaja.util.HolidayConfig;
import de.ufinke.cubaja.util.Period;
import de.ufinke.cubaja.util.Util;

public class Main extends Application {

  static public void main(String[] args) {
    
    new Main().start();
  }
  
  public void work() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setBaseName(Main.class.getPackage().getName().replace('.', '/') + "/holiday_config");
    
    HolidayConfig config = configurator.configure(new HolidayConfig());
    HolidayCalendar holidays = new HolidayCalendar(config);
    
    Period period = new Period(new Day(2010, 1, 1), new Day(2011, 12, 31));
    
    for (Date date : period) {
      if (holidays.isHoliday(date)) {
        System.out.println(Util.format(date, "yyyy-MM-dd"));
      }
    }
  }
}
