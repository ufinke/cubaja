package de.ufinke.cubaja.csv.test.example1;

public class Data {

  private String text;
  private int number;
  private TestEnum choice;

  public Data() {

  }

  public String getText() {

    return text;
  }

  public void setText(String text) {

    this.text = text;
  }

  public int getNumber() {

    return number;
  }

  public void setNumber(int number) {

    this.number = number;
  }

  public TestEnum getChoice() {
  
    return choice;
  }
  
  public void setChoice(TestEnum choice) {
  
    this.choice = choice;
  }
}
