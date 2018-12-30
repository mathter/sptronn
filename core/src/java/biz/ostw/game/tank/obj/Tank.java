package biz.ostw.game.tank.obj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;

import biz.ostw.game.tank.SideOfLight;
import biz.ostw.libgdx.DrawUtils;

public class Tank implements Spatial, Drawable, Updatable {

    private final float HALF_SIZE = 250;

    private final Body body;

    private final Sprite bodySprite;

    private final Animation<TextureRegion> trackAnimation;

    private float time = 0;

    private float speed = 0;

    private SideOfLight sideOfLight = SideOfLight.NORTH;

    Tank(Body body, TextureRegion textureSprite, Animation<TextureRegion> trackAnimation) {

        this.body = body;
        this.body.setActive(true);
        this.bodySprite = new Sprite(textureSprite);
//        MassData md = new MassData();
//        md.mass = 10;
//        this.body.setMassData(md);

        this.trackAnimation = trackAnimation;

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(HALF_SIZE * DrawUtils.MPP, HALF_SIZE * DrawUtils.MPP);
        Fixture fixture = body.createFixture(shape, 1f);

        shape.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        Vector2 p = this.body.getPosition();
        p.x = p.x * DrawUtils.PPM;
        p.y = p.y * DrawUtils.PPM;
        p.add(-HALF_SIZE, -HALF_SIZE);
        this.bodySprite.setPosition(p.x, p.y);
        this.bodySprite.draw(batch);


        if (this.speed != 0) {
            this.time += Gdx.graphics.getDeltaTime();
        }

        TextureRegion frame = this.trackAnimation.getKeyFrame(this.time);
        Transform transform = this.body.getTransform();

        batch.draw(frame,
                p.x,
                p.y,
                this.bodySprite.getWidth() / 2,
                this.bodySprite.getHeight() / 2,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                1,
                1,
                this.bodySprite.getRotation());

        batch.draw(frame,
                p.x + this.bodySprite.getWidth() - 110,
                p.y,
                -(this.bodySprite.getWidth()) / 2 + 110,
                this.bodySprite.getHeight() / 2,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                1,
                1,
                this.bodySprite.getRotation());
    }

    @Override
    public void setSideOfLight(SideOfLight sideOfLight) {
        this.sideOfLight = sideOfLight;

        final Transform transform = this.body.getTransform();

        switch (sideOfLight) {
            case NORTH:

                this.body.setTransform(transform.getPosition(), 0);
                this.bodySprite.setRotation(0);
                break;

            case EAST:
                this.body.setTransform(transform.getPosition(), MathUtils.PI * 3 / 2);
                this.bodySprite.setRotation(270);
                break;

            case SOUTH:
                this.body.setTransform(transform.getPosition(), MathUtils.PI);
                this.bodySprite.setRotation(180);
                break;

            case WEST:
                this.body.setTransform(transform.getPosition(), -MathUtils.PI * 2);
                this.bodySprite.setRotation(90);
                break;
        }
    }

    @Override
    public SideOfLight getSideOfLight() {
        return this.sideOfLight;
    }

    @Override
    public void setPosition(Vector2 position) {
        final Transform transform = this.body.getTransform();

        this.body.setTransform(position, transform.getRotation());
    }

    @Override
    public Vector2 getPosition() {
        return this.body.getPosition();
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.trackAnimation.setFrameDuration(0.2f * speed);

        this.speed = speed * DrawUtils.MPP;
        Vector2 v = new Vector2(0, this.speed);
        v.rotate(this.bodySprite.getRotation());
        this.body.setLinearVelocity(v);
    }

    @Override
    public void update(float delta) {
    }
}
