package net.sailware.drop

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport

import scala.jdk.CollectionConverters._

class Main extends ApplicationListener:

  var backgroundTexture: Texture = null
  var bucketSprite: Sprite = null
  var dropTexture: Texture = null
  var dropSound: Sound = null
  var music: Music = null

  var spriteBatch: SpriteBatch = null
  var viewport: FitViewport = null

  var touchPos: Vector2 = null

  var dropSprites: Array[Sprite] = null

  var dropTimer: Float = 0F

  var bucketRectangle: Rectangle = null
  var dropRectangle: Rectangle = null

  override def create(): Unit =
    backgroundTexture = Texture("background.png")
    dropTexture = Texture("drop.png")

    dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"))
    music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"))

    spriteBatch = SpriteBatch()
    viewport = FitViewport(8, 5)

    bucketSprite = Sprite(Texture("bucket.png"))
    bucketSprite.setSize(1, 1)

    touchPos = new Vector2()

    dropSprites = Array()

    bucketRectangle = Rectangle()
    dropRectangle = Rectangle()

    music.setLooping(true)
    music.setVolume(0.5F)
    music.play()

  override def resize(width: Int, height: Int): Unit =
    viewport.update(width, height, true)

  override def render(): Unit =
    input()
    logic()
    draw()

  private def input(): Unit =
    val delta = Gdx.graphics.getDeltaTime()
    val speed = 4F

    if Gdx.input.isKeyPressed(Input.Keys.RIGHT) then
      bucketSprite.translateX(speed * delta)
    else if Gdx.input.isKeyPressed(Input.Keys.LEFT) then
      bucketSprite.translateX(-speed * delta)

    if(Gdx.input.isTouched()) then
      touchPos.set(Gdx.input.getX().toFloat, Gdx.input.getY().toFloat)
      viewport.unproject(touchPos)
      bucketSprite.setCenterX(touchPos.x)

  private def logic(): Unit =
    val worldWidth = viewport.getWorldWidth()
    val bucketWidth = bucketSprite.getWidth()
    val bucketHeight = bucketSprite.getHeight()

    bucketSprite.setX(MathUtils.clamp(bucketSprite.getX().toFloat, 0, worldWidth - bucketWidth))
    bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight)

    val delta = Gdx.graphics.getDeltaTime()
    for i <- dropSprites.size -1 to 0 by -1 do
      val sprite = dropSprites.get(i)
      val dropWidth = sprite.getWidth()
      val dropHeight = sprite.getHeight()
      sprite.translateY(-2F * delta)
      dropRectangle.set(sprite.getX(), sprite.getY(), dropWidth, dropHeight)

      if sprite.getY() < -dropHeight then
        dropSprites.removeIndex(i)
      else if bucketRectangle.overlaps(dropRectangle) then
        dropSprites.removeIndex(i)
        dropSound.play()

    dropTimer += delta
    if dropTimer > 1F then
      dropTimer = 0F
      createDroplet()

  private def draw(): Unit =
    ScreenUtils.clear(Color.BLACK)
    viewport.apply()
    spriteBatch.setProjectionMatrix(viewport.getCamera().combined)
    spriteBatch.begin()

    val worldHeight = viewport.getWorldHeight()
    val worldWidth = viewport.getWorldWidth()

    spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight)
    bucketSprite.draw(spriteBatch)

    for drop <- dropSprites.asScala do
      drop.draw(spriteBatch)

    spriteBatch.end()

  private def createDroplet(): Unit =
    val width = 1F
    val height = 1F
    val worldWidth = viewport.getWorldWidth()
    val worldHeight = viewport.getWorldHeight()

    val sprite = Sprite(dropTexture)
    sprite.setSize(width, height)
    sprite.setX(MathUtils.random(0F, worldWidth - width))
    sprite.setY(worldHeight)
    dropSprites.add(sprite)

  override def pause(): Unit = {}
  override def resume(): Unit = {}
  override def dispose(): Unit = {}
