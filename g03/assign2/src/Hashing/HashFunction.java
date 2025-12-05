package Hashing;

public interface HashFunction {

  /**
   *Calculate hash value
   *@ param text
   *@ return result
   * @since 0.0.1
   */
  int hash(String text);

}