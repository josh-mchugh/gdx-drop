package net.sailware.drop

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport

class Main extends ApplicationListener:

  var backgroundTexture: Texture = null
  var bucketTexture: Texture = null
  var dropTexture: Texture = null
  var dropSound: Sound = null
  var music: Music = null

  var spriteBatch: SpriteBatch = null
  var viewport: FitViewport = null

  override def create(): Unit =
    backgroundTexture = Texture("background.png")
    bucketTexture = Texture("bucket.png")
    dropTexture = Texture("drop.png")

    dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"))
    music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"))

    spriteBatch = SpriteBatch()
    viewport = FitViewport(8, 5)

  override def resize(width: Int, height: Int): Unit =
    viewport.update(width, height, true)

  override def render(): Unit =
    input()
    logic()
    draw()

  private def input(): Unit = {}

  private def logic(): Unit = {}

  private def draw(): Unit =
    ScreenUtils.clear(Color.BLACK)
    viewport.apply()
    spriteBatch.setProjectionMatrix(viewport.getCamera().combined)
    spriteBatch.begin()

    val worldHeight = viewport.getWorldHeight()
    val worldWidth = viewport.getWorldWidth()

    spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight)
    spriteBatch.draw(bucketTexture, 0, 0, 1, 1)

    spriteBatch.end()

  override def pause(): Unit = {}
  override def resume(): Unit = {}
  override def dispose(): Unit = {}
