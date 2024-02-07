package ec.edu.utpl.computacion.pfr.pi // Definición del paquete

import com.github.tototoshi.csv._ // Importación de la librería para manejo de archivos CSV
import org.nspl._ // Importación de la librería para generación de gráficos
import org.nspl.awtrenderer._ // Importación del renderizador AWT para los gráficos
import org.nspl.data.HistogramData // Importación de la clase HistogramData para manejar datos de histogramas
import java.io.File // Importación de la clase File para manejo de archivos en Java

object AlineacionesXTorneo { // Definición de un objeto llamado AlineacionesXTorneo

  def main(args: Array[String]): Unit = { // Definición de la función main
    val pathDataFile2: String = "C://Users//Vicente//Downloads//ArchivoPIntegrador//dsAlineacionesXTorneo.csv" // Ruta del archivo CSV
    val reader2 = CSVReader.open(new File(pathDataFile2)) // Apertura del archivo CSV
    val contentFile2: List[Map[String, String]] = reader2.allWithHeaders() // Lectura del contenido del archivo CSV
    reader2.close() // Cierre del archivo CSV

    charting(contentFile2) // Llamada a la función charting con el contenido del archivo como argumento
  }

  def charting(data: List[Map[String, String]]): Unit = { // Definición de la función charting
    // Filtrado y mapeo de los datos para obtener una lista de los números de camiseta de los delanteros que no son 0
    val listNroShirt: List[Double] = data
      .filter(row => row("squads_position_name") == "forward" && row("squads_shirt_number") != "0")
      .map(row => row("squads_shirt_number").toDouble)

    // Cálculo del valor mínimo y máximo de los números de camiseta
    val minShirtNumber = listNroShirt.min
    val maxShirtNumber = listNroShirt.max

    // Cálculo del número de intervalos (bins) necesario para mostrar cada número de camiseta de uno en uno
    val numBins = (maxShirtNumber - minShirtNumber + 1).toInt

    // Generación de un histograma de todos los números de camiseta utilizando los datos de listNroShirt y el número de bins calculado
    val histAllShirtNumbers = xyplot(HistogramData(listNroShirt, numBins) -> bar())(
      par
        .xlab("Shirt number")
        .ylab("Frequency")
        .main("Todos los numeros Camisetas")
    )

    // Conteo de la frecuencia de cada número de camiseta
    val shirtNumberCounts = listNroShirt.groupBy(identity).mapValues(_.size).toList

    // Selección de los tres números de camiseta más comunes, ordenados por frecuencia de manera descendente
    val top3ShirtNumbers = shirtNumberCounts.sortBy(-_._2).take(3)

    // Impresión de los tres números de camiseta más usados
    println("Top 3 camisetas mas usadas:")
    top3ShirtNumbers.foreach(println)

    // Generación de un histograma para los tres números de camiseta más comunes utilizando los datos de top3ShirtNumbers y el número de bins calculado
    val top3ShirtNumbersData = top3ShirtNumbers.map { case (shirtNumber, count) => List.fill(count)(shirtNumber) }.flatten
    val histTop3ShirtNumbers = xyplot(HistogramData(top3ShirtNumbersData, numBins) -> line())(
      par
        .xlab("Shirt number")
        .ylab("Frequency")
        .main("Top 3 numeros camisetas mas usadas")
    )

    // Selección de las tres camisetas menos usadas, ordenadas por frecuencia de manera ascendente
    val leastUsedShirtNumbers = shirtNumberCounts.sortBy(_._2).take(3)

    // Impresión de las tres camisetas menos usadas
    println("Top 3 camisetas menos usadas:")
    leastUsedShirtNumbers.foreach(println)

    // Generación de un histograma para las tres camisetas menos usadas utilizando los datos de leastUsedShirtNumbers y el número de bins calculado
    val leastUsedShirtNumbersData = leastUsedShirtNumbers.map { case (shirtNumber, count) => List.fill(count)(shirtNumber) }.flatten
    val histLeastUsedShirtNumbers = xyplot(HistogramData(leastUsedShirtNumbersData, numBins) -> line())(
      par
        .xlab("Shirt number")
        .ylab("Frequency")
        .main("Top 3 camisetas menos usadas")
    )

    // Guardado de los histogramas como archivos PNG
    pngToFile(new File("C://Users//Vicente//Downloads//ArchivoPIntegrador//Histogramas Camisetas//all_shirt_numbers.png"), histAllShirtNumbers.build, 1000)
    pngToFile(new File("C://Users//Vicente//Downloads//ArchivoPIntegrador//Histogramas Camisetas//top_3_shirt_numbers.png"), histTop3ShirtNumbers.build, 1000)
    pngToFile(new File("C://Users//Vicente//Downloads//ArchivoPIntegrador//Histogramas Camisetas//least_used_shirt_numbers.png"), histLeastUsedShirtNumbers.build, 1000)
  }
}
