package Iterator;

import GenericGroup.Group;

import java.util.Iterator;

public class IteratorImpl<T> implements Iterator<T> {

    private int index;
    private Group<T> group;

    public IteratorImpl(Group<T> group) {

        this.group = group;
    }

    public boolean hasNext() {

        return index < group.size();
    }

    public T next() {

        return group.get(index++);
    }

}
