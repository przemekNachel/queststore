package template;

import org.thymeleaf.context.Context;

public class ViewData {

    private final String name;
    private final Context context;

    public ViewData(String name, Context context) {
        this.name = name;
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public void setVariable(String name, Object variable) {
        context.setVariable(name, variable);
    }

    public Context getContext() {
        return context;
    }
}
