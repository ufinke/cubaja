package de.ufinke.cubaja.util.test.example1;

import de.ufinke.cubaja.config.Configurator;
import de.ufinke.cubaja.util.Application;
import de.ufinke.cubaja.util.Day;
import de.ufinke.cubaja.util.HolidayCalendar;
import de.ufinke.cubaja.util.HolidayConfig;

public class Main extends Application {

  static public void main(String[] args) {
    
    new Main().start();
  }
  
  public void execute() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setBaseName(Main.class.getPackage().getName().replace('.', '/') + "/holiday_config");
    
    HolidayConfig config = configurator.configure(new HolidayConfig());
    HolidayCalendar holidays = new HolidayCalendar(config);
    
    Day current = new Day(2010, 1, 1);
    Day nextYear = current.clone().addYears(1);
    while (current.compareTo(nextYear) < 0) {
      if (holidays.isHoliday(current)) {
        System.out.println(current);
      }
      current.addDays(1);
    }
  }
}
