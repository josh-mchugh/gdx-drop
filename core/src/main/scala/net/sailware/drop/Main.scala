package net.sailware.drop

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport

import scala.jdk.CollectionConverters._

class Main extends Game:

  var batch: SpriteBatch = null
  var font: BitmapFont = null
  var viewport: FitViewport = null

  override def create(): Unit =
    batch = SpriteBatch()
    font = BitmapFont()
    viewport = new FitViewport(8, 5)

    font.setUseIntegerPositions(false)
    font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight())

    this.setScreen(new MainMenuScreen(this))

  override def render(): Unit =
    super.render()

  override def dispose(): Unit =
    batch.dispose()
    font.dispose()

class MainMenuScreen(
  val game: Main
) extends Screen:

  override def render(delta: Float): Unit =
    ScreenUtils.clear(Color.BLACK)

    game.viewport.apply()
    game.batch.setProjectionMatrix(game.viewport.getCamera().combined)

    game.batch.begin()
    game.font.draw(game.batch, "Welcome to Drop!! ", 1, 1.5F)
    game.font.draw(game.batch, "Tap anywhere to begin!", 1, 1)
    game.batch.end()

    if Gdx.input.isTouched() then
      game.setScreen(GameScreen(game))
      dispose()

  override def resize(width: Int, height: Int): Unit =
    game.viewport.update(width, height, true)

  override def dispose(): Unit = {}
  override def hide(): Unit = {}
  override def pause(): Unit = {}
  override def show(): Unit = {}
  override def resume(): Unit = {}

class GameScreen(
  game: Main
) extends Screen:

  val backgroundTexture= Texture("background.png")
  val dropTexture = Texture("drop.png")

  val bucketTexture = Texture("bucket.png")
  val bucketSprite = Sprite(bucketTexture)
  bucketSprite.setSize(1, 1)

  val viewport = FitViewport(8, 5)

  val touchPos = new Vector2()

  val dropSprites = Array[Sprite]()

  val bucketRectangle = Rectangle()
  val dropRectangle = Rectangle()

  val dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"))
  val music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"))
  music.setLooping(true)
  music.setVolume(0.5F)

  var dropTimer: Float = 0F
  var dropsGathered: Int = 0

  override def show(): Unit =
    music.play()

  override def render(delta: Float): Unit =
    input(delta)
    logic(delta)
    draw()

  private def input(delta: Float): Unit =
    val speed = 4F

    if Gdx.input.isKeyPressed(Input.Keys.RIGHT) then
      bucketSprite.translateX(speed * delta)
    else if Gdx.input.isKeyPressed(Input.Keys.LEFT) then
      bucketSprite.translateX(-speed * delta)

    if(Gdx.input.isTouched()) then
      touchPos.set(Gdx.input.getX().toFloat, Gdx.input.getY().toFloat)
      viewport.unproject(touchPos)
      bucketSprite.setCenterX(touchPos.x)

  private def logic(delta: Float): Unit =
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
        dropsGathered += 1
        dropSprites.removeIndex(i)
        dropSound.play()

    dropTimer += delta
    if dropTimer > 1F then
      dropTimer = 0F
      createDroplet()

  private def draw(): Unit =
    ScreenUtils.clear(Color.BLACK)
    viewport.apply()
    game.batch.setProjectionMatrix(viewport.getCamera().combined)
    game.batch.begin()

    val worldHeight = viewport.getWorldHeight()
    val worldWidth = viewport.getWorldWidth()

    game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight)
    bucketSprite.draw(game.batch)

    game.font.draw(game.batch, s"Drops collected: $dropsGathered", 0F, worldHeight )

    for drop <- dropSprites.asScala do
      drop.draw(game.batch)

    game.batch.end()

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

  override def resize(width: Int, height: Int): Unit =
    viewport.update(width, height, true)

  override def dispose(): Unit =
    backgroundTexture.dispose()
    dropSound.dispose()
    music.dispose()
    dropTexture.dispose()
    bucketTexture.dispose()

  override def hide(): Unit = {}
  override def pause(): Unit = {}
  override def resume(): Unit = {}
