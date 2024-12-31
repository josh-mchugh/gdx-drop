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
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport

class Main extends ApplicationListener:

  var backgroundTexture: Texture = null
  var bucketSprite: Sprite = null
  var dropTexture: Texture = null
  var dropSound: Sound = null
  var music: Music = null

  var spriteBatch: SpriteBatch = null
  var viewport: FitViewport = null

  val touchPos = Vector2()

  override def create(): Unit =
    backgroundTexture = Texture("background.png")
    dropTexture = Texture("drop.png")

    dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"))
    music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"))

    spriteBatch = SpriteBatch()
    viewport = FitViewport(8, 5)

    bucketSprite = Sprite(Texture("bucket.png"))
    bucketSprite.setSize(1, 1)

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

  private def logic(): Unit = {}

  private def draw(): Unit =
    ScreenUtils.clear(Color.BLACK)
    viewport.apply()
    spriteBatch.setProjectionMatrix(viewport.getCamera().combined)
    spriteBatch.begin()

    val worldHeight = viewport.getWorldHeight()
    val worldWidth = viewport.getWorldWidth()

    spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight)
    bucketSprite.draw(spriteBatch)

    spriteBatch.end()

  override def pause(): Unit = {}
  override def resume(): Unit = {}
  override def dispose(): Unit = {}
