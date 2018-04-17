package template;

import level.LevelService;
import user.codecooler.CodecoolerModel;
import user.user.User;

public class CodecoolerTemplateResolver {

    private ViewData template;
    private CodecoolerModel codecooler;

    public CodecoolerTemplateResolver(ViewData template, User codecooler) {
        this.template = template;
        this.codecooler = (CodecoolerModel) codecooler;
    }

    public ViewData getTemplate() {
        initializeVariables();
        return template;
    }

    private void initializeVariables() {
        new LevelService().initializeLevels();
        template.setVariable("user", codecooler);
        template.setVariable("level", codecooler.getLevel().getCurrentLevel());
        template.setVariable("money", codecooler.getWallet().getBalance());
    }
}
