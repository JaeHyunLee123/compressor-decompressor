import java.io.*;
import java.util.*;

import javax.print.attribute.standard.PrintQuality;

class Compression {
  public static void main(String[] args) {
    // 코드 실행시 파일 이름을 인자로 받는다
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
    // Symbol 객체는 배열에 저장하고 후에 우선순위 큐에 저장
    ArrayList<Symbol> symbolArray = new ArrayList<Symbol>();
    // symbolArray와 usedSymbol의 인덱스가 같으면 글자도 같다
    ArrayList<Character> usedSymbol = new ArrayList<Character>();

    try {
      fin.close();
    } catch (IOException e) {
      System.out.println("Error closing file");
    }
  }
}

class Symbol implements Comparable<Symbol> {
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

  @Override
  public int compareTo(Symbol another) {
    return this.frequency - another.symbol;
  }
}

// Priority queue for symbol
class SymbolPQ {
  public ArrayList<Symbol> pq;

  public SymbolPQ(ArrayList<Symbol> symbolArray) {
    pq = symbolArray;
    Collections.sort(pq);
  }

  public void insert(Symbol newSymbol) {
    pq.add(newSymbol);
    int length = pq.size();
    for (int i = length; i > 0; i--) {
      if (pq.get(i - 1).frequency > pq.get(i).frequency) {
        Symbol temp = pq.get(i);
        pq.add(i, pq.get(i - 1));
        pq.add(i - 1, temp);
      } else {
        break;
      }
    }
  }
}