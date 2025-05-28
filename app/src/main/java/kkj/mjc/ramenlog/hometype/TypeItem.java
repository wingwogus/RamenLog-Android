package kkj.mjc.ramenlog.hometype;

import androidx.annotation.DrawableRes;

public class TypeItem {
    public @DrawableRes int imageRes;
    public String name, desc;

    public TypeItem(@DrawableRes int imageRes, String name, String desc) {
        this.imageRes = imageRes;
        this.name     = name;
        this.desc     = desc;
    }
}
