package fr.maxlego08.itemstacker.zcore.utils.nms;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum NmsVersion {

    V_1_8_8(188),
    V_1_9(190),
    V_1_10(1100),
    V_1_11(1110),
    V_1_12(1120),
    V_1_12_2(1122),
    V_1_13(1130),
    V_1_13_1(1131),
    V_1_13_2(1132),
    V_1_14(1140),
    V_1_14_1(1141),
    V_1_14_2(1142),
    V_1_14_3(1143),
    V_1_14_4(1144),
    V_1_15(1150),
    V_1_15_1(1151),
    V_1_15_2(1152),
    V_1_16(1160),
    V_1_16_1(1161),
    V_1_16_2(1162),
    V_1_16_3(1163),
    V_1_16_4(1164),
    V_1_16_5(1165),
    V_1_17(1170),
    V_1_17_1(1171),
    V_1_17_2(1172),
    V_1_18(1180),
    V_1_18_1(1181),
    V_1_18_2(1182),
    V_1_19(1190),
    V_1_19_1(1191),
    V_1_19_2(1192),
    V_1_20(1200),
    V_1_20_1(1201),
    V_1_20_2(1202),
    V_1_20_3(1203),
    V_1_20_4(1204),
    V_1_20_5(1205),
    V_1_20_6(1206),
    V_1_21(1210),
    V_1_21_1(1211),
    V_1_21_2(1212),
    V_1_21_3(1213),

    ;

    /**
     * La version la plus recente connue de cette enumeration. Declaree avant
     * {@link #nmsVersion} pour etre deja initialisee quand
     * {@link #getCurrentVersion()} s'execute. Ajouter une constante en fin de
     * liste suffit a la mettre a jour, il n'y a rien d'autre a maintenir.
     */
    private static final NmsVersion HIGHEST = values()[values().length - 1];

    public static final NmsVersion nmsVersion = getCurrentVersion();
    private final int version;

    NmsVersion(int version) {
        this.version = version;
    }

    /**
     * Gets the current version of the Bukkit server.
     * <p>
     * La version est lue au debut de {@link Bukkit#getBukkitVersion()}
     * ("1.21.3-R0.1-SNAPSHOT", "26.2-R0.1-SNAPSHOT", ...). Tout ce que cette
     * enumeration ne connait pas -- une version majeure qui n'est plus "1.", une
     * version plus recente que {@link #HIGHEST}, ou une chaine illisible --
     * resout vers {@link #HIGHEST}. Il ne faut jamais retomber sur une version
     * legacy : ces chemins appellent Material#getId(), qui leve une exception sur
     * un serveur moderne et empeche le plugin de demarrer.
     *
     * @return The NmsVersion instance corresponding to the current version.
     */
    public static NmsVersion getCurrentVersion() {
        Matcher matcher = Pattern.compile("^(?<major>\\d+)(?:\\.(?<minor>\\d+))?(?:\\.(?<patch>\\d+))?").matcher(Bukkit.getBukkitVersion());

        // Version illisible, ou Minecraft 26.1 et plus recent qui abandonnent le
        // prefixe "1." : dans les deux cas le serveur est au moins aussi recent
        // que la derniere version connue
        if (!matcher.find() || !matcher.group("major").equals("1") || matcher.group("minor") == null) return HIGHEST;

        String patch = matcher.group("patch");
        int currentVersion = Integer.parseInt("1" + matcher.group("minor") + (patch == null ? "0" : patch));

        // Plus recent que toutes les versions connues (1.21.4 et au-dela)
        if (currentVersion >= HIGHEST.version) return HIGHEST;

        // Returns the version closest to the current version
        return java.util.Arrays.stream(values()).min(java.util.Comparator.comparingInt(v -> Math.abs(v.version - currentVersion))).orElse(HIGHEST);
    }

    /**
     * Checks if the current version supports PlayerProfiles.
     *
     * @return True if PlayerProfiles are supported, else False.
     */
    public boolean hasPlayerProfiles() {
        return version >= 1181;
    }

    /**
     * Checks if the current version uses obfuscated names.
     *
     * @return True if names are obfuscated, else False.
     */
    public boolean hasObfuscatedNames() {
        return version >= 1170;
    }

    /**
     * Checks if the current version supports components.
     *
     * @param isPaper True if the server uses Paper, else False.
     * @return True if components are supported, else False.
     */
    public boolean isComponent(boolean isPaper) {
        return isPaper && version >= 1165;
    }

    /**
     * Checks if the current version is a legacy item version.
     *
     * @return True if the version is legacy, else False.
     */
    public boolean isItemLegacy() {
        return version < 1130;
    }

    /**
     * Checks if the current version supports PersistentDataContainer.
     *
     * @return True if PersistentDataContainer is supported, else False.
     */
    public boolean isPdcVersion() {
        return version >= 1140;
    }

    /**
     * Checks if the current version is a legacy version for Skull owners.
     *
     * @return True if the version is legacy, else False.
     */
    public boolean isSkullOwnerLegacy() {
        return version <= 1120;
    }

    /**
     * Checks if the current version supports CustomModelData.
     *
     * @return True if CustomModelData is supported, else False.
     */
    public boolean isCustomModelData() {
        return version >= 1140;
    }

    /**
     * Checks if the current version is a hexadecimal version.
     *
     * @return True if the version is hexadecimal, else False.
     */
    public boolean isHexVersion() {
        return version >= 1160;
    }

    /**
     * Checks if the current version is an Attribute version.
     *
     * @return True if the version is Attribute, else False.
     */
    public boolean isAttributeVersion() {
        return version != 1880;
    }

    /**
     * Gets the version number associated with the enumeration.
     *
     * @return The version number.
     */
    public int getVersion() {
        return version;
    }

    public boolean isAttributItemStack() {
        return version >= 1205;
    }

    /**
     * Checks if the current version exposes the item name component on ItemMeta.
     * <p>
     * ItemMeta#hasItemName() et ItemMeta#getItemName() n'existent qu'a partir de
     * 1.20.5. Les appeler sur un serveur plus ancien leve un NoSuchMethodError,
     * alors que plugin.yml annonce api-version 1.20.
     *
     * @return True if the item name component is supported, else False.
     */
    public boolean hasItemNameApi() {
        return version >= V_1_20_5.version;
    }

    public boolean isOneHand() {
        return version == 188;
    }

    public boolean isBarrel() {
        return version >= V_1_14.version;
    }

    public boolean isShulker() {
        return version >= V_1_9.version;
    }

    public boolean isNewMaterial() {
        return version >= V_1_13.version;
    }

    public boolean isNewNBTVersion() {
        return version >= V_1_18.version;
    }

    public boolean isNewHeadApi() {
        return version >= V_1_20.version;
    }

    public boolean isNewNMSVersion() {
        return version >= V_1_17.version;
    }

}
