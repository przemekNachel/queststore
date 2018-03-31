package iterator;

import main.java.com.nwo.queststore.model.GroupModel;

import java.util.Iterator;

public class IteratorImpl<T> implements Iterator<T> {

    private int index;
    private GroupModel<T> groupModel;

    public IteratorImpl(GroupModel<T> groupModel) {

        this.groupModel = groupModel;
    }

    public boolean hasNext() {

        return index < groupModel.size();
    }

    public T next() {

        return groupModel.get(index++);
    }

}
