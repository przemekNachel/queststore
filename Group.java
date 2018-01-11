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

  public void add(T elem) {

    group.add(elem);
  }

  public void remove(T elem) {

    group.remove(elem);
  }

  public T get(int index) {

    return group.get(index);
  }

  public ArrayList<T> getGroup() {

    return group;
  }

  public int size() {

    return group.size();
  }
}
