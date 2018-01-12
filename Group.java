import java.util.ArrayList;

public class Group<T> {

  private String name;
  private ArrayList<T> group;

  public Group(String name) {

    group = new ArrayList<T>();
    setName(name);
  }

  public IteratorImpl<T> getIterator() {

    return new IteratorImpl<T>(this);
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public boolean add(T elem) {

    if (group.contains(elem)) {

      return false;
    }

    return group.add(elem);
  }

  public boolean remove(T elem) {

    return group.remove(elem);
  }

  public T get(int index) {

    return group.get(index);
  }

  public boolean contains(T elem) {

    return group.contains(elem);
  }

  public ArrayList<T> getGroup() {

    return group;
  }

  public int size() {

    return group.size();
  }

  public String toString() {

    String result = "";
    IteratorImpl<T> iter = getIterator();
    while (iter.hasNext()) {

      result += "\n  " + this.name + ": " + iter.next().toString();
    }
    return result;
  }
}
