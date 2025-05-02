package pl.edu.agh.miss.geobarriersim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;
import java.util.Map;

public class Fonts {

    private Fonts() {}
    private static final Map<Integer, BitmapFont> fonts = new HashMap<>();
    public static void dispose() {
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
    }
    public static BitmapFont getFont(int size) {
        if (fonts.containsKey(size)) {
            return fonts.get(size);
        } else {
            BitmapFont font = generate(size);
            fonts.put(size, font);
            return font;
        }
    }

    private static BitmapFont generate(int fontSize) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Lato-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.genMipMaps = true;
        parameter.size = fontSize;
        parameter.color = Color.WHITE;
        parameter.magFilter = Texture.TextureFilter.MipMapLinearLinear;
        parameter.minFilter = Texture.TextureFilter.MipMapLinearLinear;

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
