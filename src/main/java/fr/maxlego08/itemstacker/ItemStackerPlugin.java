package fr.maxlego08.itemstacker;

import fr.maxlego08.itemstacker.api.ItemManager;
import fr.maxlego08.itemstacker.api.TranslationManager;
import fr.maxlego08.itemstacker.command.commands.CommandItem;
import fr.maxlego08.itemstacker.placeholder.LocalPlaceholder;
import fr.maxlego08.itemstacker.save.Config;
import fr.maxlego08.itemstacker.save.MessageLoader;
import fr.maxlego08.itemstacker.zcore.ZPlugin;
import fr.maxlego08.itemstacker.zcore.utils.plugins.Metrics;
import fr.maxlego08.itemstacker.zcore.utils.plugins.VersionChecker;

/**
 * System to create your plugins very simple Projet:
 * <a href="https://github.com/Maxlego08/TemplatePlugin">https://github.com/Maxlego08/TemplatePlugin</a>
 *
 * @author Maxlego08
 */
public class ItemStackerPlugin extends ZPlugin {

    private final ItemManager itemManager = new ZItemManager(this);
    private final TranslationManager translationManager = new ZTranslationManager(this);

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zitemstacker");

        this.preEnable();

        this.registerCommand("zitemstacker", new CommandItem(this));

        saveDefaultConfig();
        Config.getInstance().load(getConfig());
        this.addSave(new MessageLoader(this));

        this.loadFiles();

        new Metrics(this, 9330);

        if (getConfig().getBoolean("enable-version-check", true)) {
            VersionChecker checker = new VersionChecker(this, 15);
            checker.checkVersion();
        }

        this.addListener(this.itemManager);
        this.translationManager.loadTranslations();

        this.postEnable();
    }

    @Override
    public void onDisable() {

        this.preDisable();

        this.saveFiles();

        this.postDisable();
    }

    @Override
    public void reloadFiles() {
        reloadConfig();
        Config.getInstance();
        super.reloadFiles();
        this.translationManager.loadTranslations();
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public TranslationManager getTranslationManager() {
        return translationManager;
    }
}
