package com.jams.jogodavelha

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jams.jogodavelha.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val buttons = Array(3) { arrayOfNulls<Button>(3) }
    private var player1Turn = true
    private var roundCount = 0
    private var player1Points = 0
    private var player2Points = 0
    private var player1Name = "Jogador 1"
    private var player2Name = "Jogador 2"
    private var player1StartsWithNextRound = true
    private var isGameActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttons[0][0] = binding.btn00
        buttons[0][1] = binding.btn01
        buttons[0][2] = binding.btn02
        buttons[1][0] = binding.btn10
        buttons[1][1] = binding.btn11
        buttons[1][2] = binding.btn12
        buttons[2][0] = binding.btn20
        buttons[2][1] = binding.btn21
        buttons[2][2] = binding.btn22

        // Escutar cliques nos botões de jogo
        for (row in buttons) {
            for (button in row) {
                button?.setOnClickListener { view ->
                    onButtonClick(view as Button)
                }
            }
        }

        // Escutar cliques no botão de reset
        binding.btnReset.setOnClickListener {
            resetGame()
        }

        // Capturar nomes dos jogadores
        getPlayerNames()
    }

    private fun onButtonClick(button: Button) {
        if (!isGameActive || button.text.toString() != "") {
            return
        }

        if (player1Turn) {
            button.text = "X"
            button.setTextColor(Color.RED)
        } else {
            button.text = "O"
            button.setTextColor(Color.BLUE)
        }

        roundCount++

        if (checkForWin()) {
            isGameActive = false
            if (player1Turn) {
                player1Wins()
            } else {
                player2Wins()
            }
        } else if (roundCount == 9) {
            // Deu velha
            isGameActive = false
            draw()
        } else {
            player1Turn = !player1Turn
            updateTurnIndicator()
        }
    }

    private fun getPlayerNames() {
        val builder = AlertDialog.Builder(this, R.style.App_AlertDialogTheme)
        val dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_player_names, null)
        val editTextP1 = dialogLayout.findViewById<EditText>(R.id.etPlayer1Name)
        val editTextP2 = dialogLayout.findViewById<EditText>(R.id.etPlayer2Name)

        builder.setView(dialogLayout)
        builder.setTitle("Insira os nomes dos jogadores")
        builder.setPositiveButton("Começar") { _, _ ->
            val p1Name = editTextP1.text.toString().uppercase()
            val p2Name = editTextP2.text.toString().uppercase()

            player1Name = if (p1Name.isNotEmpty()) p1Name else "JOGADOR 1"
            player2Name = if (p2Name.isNotEmpty()) p2Name else "JOGADOR 2"

            updatePlayerNamesOnScreen()
            updateTurnIndicator()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun updatePlayerNamesOnScreen() {
        binding.tvPlayer1Name.text = "$player1Name (X)"
        binding.tvPlayer2Name.text = "$player2Name (O)"
    }

    private fun updateTurnIndicator() {
        if (player1Turn) {
            binding.tvPlayer.text = player1Name
            binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.light_red))
        } else {
            binding.tvPlayer.text = player2Name
            binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue))
        }
    }

    private fun checkForWin(): Boolean {
        val field = Array(3) { Array(3) { "" } }

        // Montar o jogo em uma matriz de String
        for (i in 0..2) {
            for (j in 0..2) {
                field[i][j] = buttons[i][j]?.text.toString()
            }
        }

        // Se ganhar por linha horizontal
        for (i in 0..2) {
            if (field[i][0] == field[i][1] && field[i][0] == field[i][2] && field[i][0] != "") {
                return true
            }
        }

        // Se ganhar por linha vertical
        for (i in 0..2) {
            if (field[0][i] == field[1][i] && field[0][i] == field[2][i] && field[0][i] != "") {
                return true
            }
        }

        // Se ganhar por diagonal principal
        if (field[0][0] == field[1][1] && field[0][0] == field[2][2] && field[0][0] != "") {
            return true
        }

        // Se ganhar por diagonal secundária
        if (field[0][2] == field[1][1] && field[0][2] == field[2][0] && field[0][2] != "") {
            return true
        }

        return false
    }

    private fun player1Wins() {
        player1Points++
        showWinDialog("$player1Name Venceu!")
    }

    private fun player2Wins() {
        player2Points++
        showWinDialog("$player2Name Venceu!")
    }

    private fun draw() {
        showWinDialog("Empate!")
    }

    private fun showWinDialog(message: String) {
        AlertDialog.Builder(this, R.style.App_AlertDialogTheme)
            .setTitle(message)
            .setPositiveButton("Continuar") { _, _ ->
                updatePointsText()
                resetBoard()
            }
            .setCancelable(false)
            .show()
    }

    private fun updatePointsText() {
        binding.tvPlayer1Pts.text = player1Points.toString()
        binding.tvPlayer2Pts.text = player2Points.toString()
    }

    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]?.text = ""
            }
        }
        roundCount = 0

        player1Turn = player1StartsWithNextRound
        player1StartsWithNextRound = !player1StartsWithNextRound

        updateTurnIndicator()

        isGameActive = true
    }

    private fun resetGame() {
        player1Points = 0
        player2Points = 0
        updatePointsText()

        player1StartsWithNextRound = true

        resetBoard()
        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
    }
}