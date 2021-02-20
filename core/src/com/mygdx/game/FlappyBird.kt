package com.mygdx.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.ScreenUtils
import java.util.*
import javax.lang.model.type.ArrayType

class FlappyBird : ApplicationAdapter() {
    var batch: SpriteBatch? = null

    var background: Texture? = null
    var flappyBird: Array<Texture>? = null

    var birdStayFlag = 0
    var gameStateFlag = 0

    var flyHeight : Float = 0f
    var fallingSpeed : Float = 0f

    var topTube : Texture?=null
    var bottomTube : Texture?=null

    var spaceBetweenTube = 500;

    var random : Random ?=null

    var tubeSpeed = 5
    var tubeX = Array(5){0.0f}
    var tubeShift = Array(5){0.0f}

    var tubesNumber = 5
    var distanceBetweenTubes = 0.0f

    var birdCircle : Circle ?= null
    var topTubeRectangles : Array<Rectangle> = Array(5){ Rectangle() }
    var bottomTubeRectangles : Array<Rectangle> = Array(5){ Rectangle() }

    var gameScore = 0
    var passedTubeIndex = 0

    lateinit var scoreFont: BitmapFont

    lateinit var gameOver : Texture



    override fun create() {
        batch = SpriteBatch()

        scoreFont = BitmapFont()
        scoreFont.color = Color.CYAN
        scoreFont.data.scale(10f)

        birdCircle = Circle()

        gameOver = Texture("game_over.png")
        background = Texture("background.png")
        flappyBird = arrayOf(Texture("bird_wings_up.png"),
                Texture("bird_wings_down.png"))
        topTube = Texture("top_tube.png")
        bottomTube = Texture("bottom_tube.png")

        random = Random()

        distanceBetweenTubes = Gdx.graphics.width.toFloat()/2
        initGame()
    }

    private fun initGame(){
        flyHeight = Gdx.graphics.height.toFloat()/2-flappyBird!![birdStayFlag].height/2
        for (i in 0..tubesNumber-1){
            tubeX[i] = Gdx.graphics.width/2 - topTube!!.width/2 + Gdx.graphics.width+  i*distanceBetweenTubes
            tubeShift[i] = (random!!.nextFloat() - 0.5f) *
                    (Gdx.graphics.height-spaceBetweenTube - 200)

            topTubeRectangles[i] = Rectangle()
            bottomTubeRectangles[i] = Rectangle()
        }
    }

    override fun render() {
        batch!!.begin()
        batch!!.draw(background, 0f,0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        if (gameStateFlag==1){
            if (tubeX[passedTubeIndex] < Gdx.graphics.width/2){
                gameScore++
                if (passedTubeIndex<tubesNumber-1) passedTubeIndex++
                else passedTubeIndex =0
            }
            if (Gdx.input.justTouched()){
               fallingSpeed = -20f
            }
            if (flyHeight>0 ){
                fallingSpeed++
                flyHeight-=fallingSpeed
            } else gameStateFlag = -1

        }else if (gameStateFlag == 0){
            if (Gdx.input.justTouched()) gameStateFlag = 1
        } else if (gameStateFlag ==-1){
            batch!!.draw(gameOver, Gdx.graphics.width.toFloat()/2 - gameOver.width/2, Gdx.graphics.height.toFloat()/2 - gameOver.height/2)
            if (Gdx.input.justTouched()){
                gameStateFlag = 1
                gameScore =0
                passedTubeIndex =0
                fallingSpeed = 0f
                initGame()
            }
        }


        birdStayFlag = if (birdStayFlag==0) 1
        else 0

        for (i in 0..tubesNumber-1) {
            if (gameStateFlag ==1) {
                if (tubeX[i] < -topTube!!.width) tubeX[i] = tubesNumber * distanceBetweenTubes
                else tubeX[i] = tubeX[i] - tubeSpeed

                batch!!.draw(topTube, tubeX[i],
                        Gdx.graphics.height.toFloat() / 2 + spaceBetweenTube / 2 + tubeShift[i])
                batch!!.draw(bottomTube, tubeX[i],
                        Gdx.graphics.height.toFloat() / 2 - spaceBetweenTube / 2 - bottomTube!!.height + tubeShift[i])

                topTubeRectangles[i] = Rectangle(tubeX[i],
                        Gdx.graphics.height.toFloat() / 2 + spaceBetweenTube / 2 + tubeShift[i],
                        topTube!!.width.toFloat(), topTube!!.height.toFloat())

                bottomTubeRectangles[i] = Rectangle(tubeX[i],
                        Gdx.graphics.height.toFloat() / 2 - spaceBetweenTube / 2 - bottomTube!!.height + tubeShift[i],
                        bottomTube!!.width.toFloat(), bottomTube!!.height.toFloat())
            }
        }


        batch!!.draw(flappyBird!![birdStayFlag],
                Gdx.graphics.width.toFloat()/2 -flappyBird!![birdStayFlag].width/2,
                flyHeight)

        scoreFont.draw(batch, gameScore.toString(), 100f, 200f)
        batch!!.end()

        birdCircle!!.set(Gdx.graphics.width.toFloat()/2, flyHeight + flappyBird!![birdStayFlag].height/2,
                (flappyBird!![birdStayFlag].width/2).toFloat())


        for (i in 0..tubesNumber-1) {
            if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) ||
                    Intersector.overlaps(birdCircle, bottomTubeRectangles[i])){
                gameStateFlag = -1
            }
        }



    }

    override fun dispose() {

    }
}