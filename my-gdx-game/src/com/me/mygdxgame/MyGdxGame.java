package com.me.mygdxgame;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGdxGame implements ApplicationListener {
    Texture dropImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;

    OrthographicCamera camera;
    SpriteBatch batch;

    Rectangle bucket;

    Array<Rectangle> raindrops;

    long lastDropTime;
    TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Matrix4 angle;

	@Override
	public void create() {

	      dropImage = new Texture(Gdx.files.internal("droplet.png"));
	      bucketImage = new Texture(Gdx.files.internal("bucket.png"));

	      dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
	      rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.wav"));

	      rainMusic.setLooping(true);
	      rainMusic.play();

	      camera = new OrthographicCamera();
	      camera.setToOrtho(false, 800, 480);

	      batch = new SpriteBatch();

	      bucket = new Rectangle();
	      bucket.x = 800 / 2 - 64 / 2;
	      bucket.y = 20;
	      bucket.width = 64;
	      bucket.height = 64;

	      raindrops = new Array<Rectangle>();
	      spawnRaindrop();

	      map = new TmxMapLoader().load("map.tmx");
	      renderer = new OrthogonalTiledMapRenderer(map, 1);

	}

	@Override
	public void dispose() {
	    dropImage.dispose();
	    bucketImage.dispose();
	    dropSound.dispose();
	    rainMusic.dispose();
	    batch.dispose();
	}

	private void spawnRaindrop() {
	    Rectangle raindrop = new Rectangle();
	    raindrop.x = MathUtils.random(0, 800-64);
	    raindrop.y = 480;
	    raindrop.width = 64;
	    raindrop.height = 64;
	    raindrops.add(raindrop);
	    lastDropTime = TimeUtils.nanoTime();
	 }

	@Override
	public void render() {
	    Gdx.gl.glClearColor(0, 0, 0.2f, 1);
	    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

	    camera.update();

	    renderer.setView(camera);
	    renderer.render();

	    batch.setProjectionMatrix(camera.combined);
	    batch.begin();
	    batch.draw(bucketImage, bucket.x, bucket.y);
	    for(Rectangle raindrop: raindrops) {
	       batch.draw(dropImage, raindrop.x, raindrop.y);
	    }
	    batch.end();

	    if(Gdx.input.isTouched()) {
	        Vector3 touchPos = new Vector3();
	        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	        camera.unproject(touchPos);
	        bucket.x = touchPos.x - 64 / 2;
	     }

	    int speed = 10;
	    if(Gdx.input.isKeyPressed(Input.Keys.UP))
	    {
	        camera.translate(0, speed);
	    }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            camera.translate(0, -speed);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            camera.translate(-speed, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            camera.translate(speed, 0);
        }

	    if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

	    Iterator<Rectangle> iter = raindrops.iterator();
	    while(iter.hasNext()) {
	       Rectangle raindrop = iter.next();
	       raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
	       if(raindrop.y + 64 < 0) iter.remove();
	       if(raindrop.overlaps(bucket)) {
	           dropSound.play();
	           iter.remove();
	        }
	    }

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
