package ec.edu.utpl.computacion.pfr.pi // Definición del paquete

import com.github.tototoshi.csv._ // Importación de la librería para manejo de archivos CSV
import org.nspl._ // Importación de la librería para generación de gráficos
import org.nspl.awtrenderer._ // Importación del renderizador AWT para los gráficos
import org.nspl.data.HistogramData // Importación de la clase HistogramData para manejar datos de histogramas
import java.io.File // Importación de la clase File para manejo de archivos en Java

object PartidosYGoles { // Definición de un objeto llamado PartidosYGoles

  // Definición de un objeto implícito que establece el formato CSV personalizado
  implicit object CustomFormat extends DefaultCSVFormat {
    override val delimiter: Char = ';' // Establecimiento del delimitador como ';'
  }

  def main(args: Array[String]): Unit = { // Definición de la función main
    val pathDataFile: String = "C://Users//Vicente//Downloads//ArchivoPIntegrador//dsPartidosYGoles.csv" // Ruta del archivo CSV
    val reader = CSVReader.open(new File(pathDataFile)) // Apertura del archivo CSV
    val contentFile: List[Map[String, String]] = reader.allWithHeaders() // Lectura del contenido del archivo CSV
    reader.close() // Cierre del archivo CSV

    processData(contentFile) // Llamada a la función processData con el contenido del archivo como argumento
  }

  def processData(data: List[Map[String, String]]): Unit = { // Definición de la función processData
    // Filtrado y mapeo de los datos para obtener una lista de los identificadores de los jugadores que marcaron goles
    val listGoals: List[Double] = data
      .filter(_.contains("result")) // Filtrado de las filas que contienen el campo "result"
      .filter(row => row("result") == "goal") // Filtrado de las filas donde el valor del campo "result" es "goal"
      .map(row => row("player_id").toDouble) // Mapeo para obtener los identificadores de los jugadores en formato Double

    // Cálculo del valor mínimo y máximo de los identificadores de los jugadores que marcaron goles
    val minPlayerID = listGoals.min
    val maxPlayerID = listGoals.max

    // Cálculo del número de intervalos (bins) necesario para mostrar cada número de identificación de jugador de uno en uno
    val numBins = (maxPlayerID - minPlayerID + 1).toInt

    // Generación de un histograma de todos los goles utilizando los datos de listGoals y el número de bins calculado
    val histAllGoals = xyplot(HistogramData(listGoals, numBins) -> bar())(
      par
        .xlab("Player ID")
        .ylab("Frequency")
        .main("Todos los goles")
    )

    // Cálculo de la frecuencia de cada identificador de jugador que marcó goles
    val playerIDCounts = listGoals.groupBy(identity).mapValues(_.size).toList

    // Selección de los tres jugadores con más goles, ordenados por cantidad de goles de manera descendente
    val top3Scorers = playerIDCounts.sortBy(-_._2).take(3)

    // Generación de un histograma para los tres jugadores principales utilizando los datos de top3Scorers y el número de bins calculado
    val top3ScorersData = top3Scorers.map { case (playerID, count) => List.fill(count)(playerID) }.flatten
    val histTop3Scorers = xyplot(HistogramData(top3ScorersData, numBins) -> bar())(
      par
        .xlab("Player ID")
        .ylab("Frequency")
        .main("Top 3 goleadores")
    )

    // Selección de los tres jugadores con menos goles, ordenados por cantidad de goles de manera ascendente
    val leastScorerPlayers = playerIDCounts.sortBy(_._2).take(3)

    // Generación de un histograma para los tres jugadores menos anotadores utilizando los datos de leastScorerPlayers y el número de bins calculado
    val leastScorerPlayersData = leastScorerPlayers.map { case (playerID, count) => List.fill(count)(playerID) }.flatten
    val histLeastScorerPlayers = xyplot(HistogramData(leastScorerPlayersData, numBins) -> bar())(
      par
        .xlab("Player ID")
        .ylab("Frequency")
        .main("Top 3 jugadores con menos goles")
    )

    // Guardado de los histogramas como archivos PNG
    pngToFile(new File("C://Users//Vicente//Downloads//ArchivoPIntegrador//Histogramas Goles//all_goals.png"), histAllGoals.build, 1000)
    pngToFile(new File("C://Users//Vicente//Downloads//ArchivoPIntegrador//Histogramas Goles//top_3_scorers.png"), histTop3Scorers.build, 1000)
    pngToFile(new File("C://Users//Vicente//Downloads//ArchivoPIntegrador//Histogramas Goles//least_scorer_players.png"), histLeastScorerPlayers.build, 1000)
  }
}
