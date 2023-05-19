import java.io.*;
import java.util.*;

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
    SymbolPQ symbolPQ = changeTxtToSymbolPQ(fin);

    Symbol test = new Symbol('Y', 2);

    symbolPQ.insert(test);

    symbolPQ.printPQ();

    // Close file
    try {
      fin.close();
    } catch (IOException e) {
      System.out.println("Error closing file");
    }
  }

  static SymbolPQ changeTxtToSymbolPQ(FileInputStream fin) {
    // 2-1. 처음 나온 글자면 새롭게 Symbol 객체 생성
    // 2-2. 2번 이상 나온 글자면 해당 Symbol 객체 frequency++
    // Symbol 객체는 배열에 저장하고 후에 우선순위 큐에 저장
    ArrayList<Symbol> symbolArray = new ArrayList<Symbol>();
    // symbolArray와 usedSymbol의 인덱스가 같으면 글자도 같다
    ArrayList<Character> usedSymbol = new ArrayList<Character>();

    try {
      int i;
      do {
        i = fin.read();
        if (i != -1) {
          int index = usedSymbol.indexOf((char) i);
          if (index == -1) {
            Symbol newSymbol = new Symbol((char) i);
            usedSymbol.add((char) i);
            symbolArray.add(newSymbol);
          } else {
            Symbol oneMore = symbolArray.get(index);
            oneMore.frequency++;
          }
        }
      } while (i != -1);
    } catch (IOException e) {
      System.out.println("Error reading file");
    }
    return new SymbolPQ(symbolArray);
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
    return this.frequency - another.frequency;
  }
}

// Priority queue for symbol
class SymbolPQ {
  public ArrayList<Symbol> pq;

  // Get array of Symbol and sort it in accending order
  public SymbolPQ(ArrayList<Symbol> symbolArray) {
    pq = symbolArray;
    Collections.sort(pq);
  }

  // insert new symbol at last index and sort
  public void insert(Symbol newSymbol) {
    pq.add(newSymbol);
    for (int i = pq.size() - 1; i > 0; i--) {
      if (pq.get(i - 1).frequency > pq.get(i).frequency) {
        Symbol temp = pq.get(i);
        pq.set(i, pq.get(i - 1));
        pq.set(i - 1, temp);
      } else {
        break;
      }
    }
  }

  // remove first element and return it
  public Symbol popFront() {
    Symbol removing = pq.get(0);
    pq.remove(0);
    return removing;
  }

  public void printPQ() {
    for (int i = 0; i < pq.size(); i++) {
      System.out.println("Symbol: " + pq.get(i).symbol + ", Frequency: " + pq.get(i).frequency);
    }
  }
}
