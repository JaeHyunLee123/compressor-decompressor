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

    // 3. Make huffman code
    Symbol huffmanCode = makeHuffmanCode(symbolPQ);
    HashMap<Character, String> codeMap = huffmanCode.makeCodeMap();
    HashMap<Character, Integer> frequencyMap = huffmanCode.makeFrequencyMap();
    Set<Character> keys = codeMap.keySet();

    String fileName = args[0] + "-compressed.htf";

    writeHeader(fileName, keys, codeMap, frequencyMap);

    printHashMap(codeMap);
    printHashMap(frequencyMap);

    // Close file
    try {
      fin.close();
    } catch (IOException e) {
      System.out.println("Error closing file");
    }
  }

  static void writeHeader(String fileName, Set<Character> keys, HashMap<Character, String> codeMap,
      HashMap<Character, Integer> frequencyMap) {
    Iterator<Character> iterator = keys.iterator();

    // just initailize
    try {
      FileWriter fileWriter = new FileWriter(fileName);
      fileWriter.write("");
      fileWriter.close();
    } catch (IOException e) {
      System.out.println("An error occurred while creating the file.");
      e.printStackTrace();
    }

    while (iterator.hasNext()) {
      char key = iterator.next();
      String content = String.valueOf(key) + "(" + String.valueOf(frequencyMap.get(key)) + "):"
          + String.valueOf(frequencyMap.get(key)) + " ";
      try {
        FileWriter fileWriter = new FileWriter(fileName, true);
        fileWriter.write(content);
        fileWriter.close();
      } catch (IOException e) {
        System.out.println("An error occurred while creating the file.");
        e.printStackTrace();
      }
    }
  }

  static <T> void printHashMap(HashMap<Character, T> map) {
    for (Map.Entry<Character, T> entry : map.entrySet()) {
      Character key = entry.getKey();
      T value = entry.getValue();
      System.out.println("Key: " + key + ", Value: " + value);
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

  static Symbol makeHuffmanCode(SymbolPQ PQ) {
    while (PQ.pq.size() > 1) {
      Symbol p = PQ.popFront();
      Symbol q = PQ.popFront();

      Symbol merged = new Symbol('M', p.frequency + q.frequency);
      merged.left = p;
      merged.right = q;

      PQ.insert(merged);
    }
    return PQ.pq.get(0);
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

  public boolean isLeaf() {
    if (this.left == null) {
      return true;
    }
    return false;
  }

  public void showHuffmanCode(String code) {
    if (this.isLeaf()) {
      System.out.println("Symbol: " + this.symbol + ", Code: " + code + ", Frequency: " + this.frequency);
    } else {
      this.left.showHuffmanCode(code + "0");
      this.right.showHuffmanCode(code + "1");
    }
  }

  private void getCode(String code, HashMap<Character, String> codeMap) {
    if (this.isLeaf()) {
      codeMap.put(this.symbol, code);
    } else {
      this.left.getCode(code + "0", codeMap);
      this.right.getCode(code + "1", codeMap);
    }
  }

  public HashMap<Character, String> makeCodeMap() {
    HashMap<Character, String> codeMap = new HashMap<>();

    this.getCode("", codeMap);

    return codeMap;
  }

  private void getFrequency(HashMap<Character, Integer> frequencyMap) {
    if (this.isLeaf()) {
      frequencyMap.put(this.symbol, this.frequency);
    } else {
      this.left.getFrequency(frequencyMap);
      this.right.getFrequency(frequencyMap);
    }
  }

  public HashMap<Character, Integer> makeFrequencyMap() {
    HashMap<Character, Integer> frequencyMap = new HashMap<>();

    this.getFrequency(frequencyMap);

    return frequencyMap;
  }

  public void showHuffmanCode() {
    this.showHuffmanCode("");
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
