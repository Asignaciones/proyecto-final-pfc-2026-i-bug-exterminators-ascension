package proyecto

import common._
import AsignacionAulas._

object AsignacionAulasPar {

  /** Versión paralela de choques: divide el vector de cursos en dos mitades. */
  def choquesPar(cursos: Cursos, a: Asignacion): Int = ???

  /** Versión paralela de desperdicio: divide el vector de cursos en dos mitades. */
  def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = ???

  /** Versión paralela de movilidad: divide el vector de cursos en dos mitades. */
  def movilidadPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                   a: Asignacion): Int = ???

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
                          w: Pesos): (Asignacion, Int) = ???
}
