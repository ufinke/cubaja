package de.ufinke.cubaja.config.test.example1;

import java.util.Date;
import de.ufinke.cubaja.config.*;

public class ValuesConfig extends ConfigNode {

  private Date date;
  private double amount;
  private Quality quality;
  private Calculator calculator;
  private int[] months;

  public ValuesConfig() {

  }

  public Date getDate() {

    return date;
  }

  public void setDate(Date date) {

    this.date = date;
  }

  public double getAmount() {

    return amount;
  }

  public void setAmount(double amount) {

    this.amount = amount;
  }

  public Quality getQuality() {

    return quality;
  }

  public void setQuality(Quality quality) {

    this.quality = quality;
  }

  public Calculator getCalculator() {

    return calculator;
  }

  public void setCalculator(Calculator calculator) {

    this.calculator = calculator;
  }

  public int[] getMonths() {

    return months;
  }

  public void setMonths(int[] months) {

    this.months = months;
  }
}
