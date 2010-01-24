package de.ufinke.cubaja.util.test.example1;

import de.ufinke.cubaja.util.*;
import de.ufinke.cubaja.config.*;
import java.util.*;

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
