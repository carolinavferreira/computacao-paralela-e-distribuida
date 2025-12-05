package Hashing;
public class Hash implements HashFunction {

  @Override
  public int hash(String text) {
      return text.hashCode();
  }

}