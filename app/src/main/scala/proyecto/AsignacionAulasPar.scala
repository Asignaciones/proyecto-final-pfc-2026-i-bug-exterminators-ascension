package proyecto

import common._
import AsignacionAulas._

object AsignacionAulasPar {

  /** Versión paralela de choques: divide el vector de cursos en dos mitades. */
  def choquesPar(cursos: Cursos, a: Asignacion): Int = {
    val n   = cursos.length
    val mid = n / 2

    def choquesRango(desde: Int, hasta: Int): Int =
      (desde until hasta).toVector.flatMap { i =>
        (i + 1 until n).toVector
          .filter(j => a(i) >= 0 && a(j) >= 0 && a(i) == a(j))
          .map(j => if (solapan(cursos(i), cursos(j))) 1 else 0)
      }.sum

    val (t1, t2) = parallel(choquesRango(0, mid), choquesRango(mid, n))
    t1 + t2
  }

  /** Versión paralela de desperdicio: divide el vector de cursos en dos mitades. */
  def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {
    val n   = cursos.length
    val mid = n / 2

    def desperdicioRango(desde: Int, hasta: Int): Int =
      (desde until hasta).toVector
        .filter(i => a(i) >= 0 && capAula(aulas(a(i))) >= estCurso(cursos(i)))
        .map(i => capAula(aulas(a(i))) - estCurso(cursos(i)))
        .sum

    val (t1, t2) = parallel(desperdicioRango(0, mid), desperdicioRango(mid, n))
    t1 + t2
  }

  /** Versión paralela de movilidad: divide el vector de cursos en dos mitades. */
  def movilidadPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                   a: Asignacion): Int = {
    val ordenados = cursos.indices.toVector
      .filter(i => a(i) >= 0)
      .sortBy(i => iniCurso(cursos(i)))

    if (ordenados.length < 2) 0
    else {
      val pares = ordenados.zip(ordenados.tail)
      val ini   = 0
      val fin   = pares.length
      val mid   = ini + (fin - ini) / 2

      val (t1, t2) = parallel(
        pares.slice(ini, mid).map { case (i, j) => d(a(i))(a(j)) }.sum,
        pares.slice(mid, fin).map { case (i, j) => d(a(i))(a(j)) }.sum
      )
      t1 + t2
    }
  }

  /**
   * Versión paralela de generarAsignaciones:
   * paraleliza la construcción usando parallel sobre los valores del primer curso.
   */
  def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = {
    if(n == 0)
      Vector(Vector())//Caso base devuelve vector de vector vacio como punto de arranque para construir las posibles asignaciones
    else {
      val anteriorAsignacion  = generarAsignacionesPar(n-1,m)

      def aux(vect: Vector[Int]): Vector[Asignacion] = {

        if(vect.length == 1)
          anteriorAsignacion.map(asigAnterior => vect(0) +: asigAnterior)
        else {
          val (vect1,vect2) = vect.splitAt(vect.length/2)
          val (a,b) = parallel(
            aux(vect1),
            aux(vect2)
          )
          a ++ b//Para unir ambos vectores en uno
        }
      }
      aux((0 until m).toVector) //Va de 0 hasta m-1 y convierte esos numeros generados a vector para pasarlos por parametro
    }

  }

  /**
   * Versión paralela de asignacionOptima:
   * divide el espacio de candidatos en dos mitades y combina los mínimos.
   */
  def asignacionOptimaPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                          w: Pesos): (Asignacion, Int) = {
    // generamos todas las asignaciones posibles
    val todasLasAsignaciones = generarAsignaciones(cursos.length, aulas.length)
    // dividimos el espacio en dos mitades
    val mitad = todasLasAsignaciones.length / 2
    val mitadIzq = todasLasAsignaciones.take(mitad)
    val mitadDer = todasLasAsignaciones.drop(mitad)
    // evaluamos cada mitad en paralelo
    val (minimoIzq, minimoDer) = parallel(
      mitadIzq.map(asig => (asig, costoAsignacion(cursos, aulas, d, asig, w))).minBy(_._2),
      mitadDer.map(asig => (asig, costoAsignacion(cursos, aulas, d, asig, w))).minBy(_._2)
    )
    // retornamos el minimo entre los dos resultados
    if (minimoIzq._2 <= minimoDer._2) minimoIzq else minimoDer
  }
}
