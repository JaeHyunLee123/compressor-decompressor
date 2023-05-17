import java.io.*;

class Compression {
  public static void main(String[] args) {
    //코드 실행시 파일 이름을 인자로 받는다
    if (args.length != 1) {
      System.out.println("Usage: Showfile filename");
      return;
    }
    
    // 1. 파일을 불러온다
    FileInputStream fin;

    try {
      fin = new FileInputStream(args[0]);
    } catch (FileNotFoundException e) {
      System.out.println("Cannot open file");
      return;
    }

    // 2. 파일 안 글자를 한 글자씩 읽으면서 각 글자가 몇 개씩 있는지 센다
    // 2-1. 처음 나온 글자면 새롭게 Symbol 객체 생성
    // 2-2. 2번 이상 나온 글자면 해당 Symbol 객체 frequency++

    try {
      fin.close();
    } catch (IOException e) {
      System.out.println("Error closing file");
    }
  }
}

class Symbol {
  public char symbol;
  public int frequency;
  public Symbol left;
  public Symbol right;

  public Symbol(char symbol, int frequency) {
    this.symbol = symbol;
    this.frequency = frequency;
    this.left = null;
    this.right = null;
  }

  public Symbol(char symbol) {
    this(symbol, 1);
  }

  public boolean isParent() {
    if (this.left == null) {
      return false;
    }
    return true;
  }
}

class SymbolPriorityQueue {
  public Symbol[] pq;

}