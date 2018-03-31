package main.java.com.nwo.queststore.model;

import java.util.Iterator;

public class MenuModel {

    private static final String INDENT = "    ";
    private GroupModel<MenuOptionModel> menuOptions;

    public MenuModel(MenuOptionModel... options) {

        menuOptions = new GroupModel<>("testmenu");
        for (MenuOptionModel mo : options) {

            menuOptions.add(mo);
        }
    }

    public String toString() {

        String result = "";
        Iterator<MenuOptionModel> iter = menuOptions.getIterator();
        while (iter.hasNext()) {

            MenuOptionModel currentOption = iter.next();
            result += "\n" + MenuModel.INDENT + currentOption.toString();
        }
        return result;
    }

    public Iterator<MenuOptionModel> getIterator() {

        return menuOptions.getIterator();
    }
}
