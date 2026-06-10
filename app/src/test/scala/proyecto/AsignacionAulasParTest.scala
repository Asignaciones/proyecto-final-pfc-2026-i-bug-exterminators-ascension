package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._
import AsignacionAulasPar._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)

  test("choquesPar: asignacion [0,0,1] tiene 1 choque") {
    assert(choquesPar(c1, Vector(0, 0, 1)) == 1)
  }

  test("choquesPar: asignacion [0,1,0] no tiene choques") {
    assert(choquesPar(c1, Vector(0, 1, 0)) == 0)
  }

  test("desperdicioPar: asignacion [0,0,1] tiene desperdicio 25") {
    assert(desperdicioPar(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("movilidadPar: asignacion [0,0,1] tiene movilidad 3") {
    assert(movilidadPar(c1, a1, d1, Vector(0, 0, 1)) == 3)
  }

  test("generarAsignacionesPar: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignacionesPar(2, 2).length == 4)
  }

  //
  test("generarAsignacionesPar: 0 cursos devuelve una asignacion vacia") {
    assert(
      generarAsignacionesPar(0,5) == Vector(Vector()))
  }

  //

  test("generarAsignaciones: contiene la asignacion Vector(0,2,1)") {
    val resultado = generarAsignaciones(3,3)
    assert(
      resultado.contains(Vector(0,2,1)))
  }
  //
  test("generarAsignaciones: ninguna asignacion tiene aulas fuera del rango caso extremo") {
    val resultado = generarAsignaciones(3,4)
    assert(resultado.forall(asignacion =>
      asignacion.forall(aula => aula >= 0 && aula < 4)))
  }
   //
  test("generarAsignaciones: todas las asignaciones son diferentes") {
    val resultado = generarAsignaciones(2,3)
    assert(resultado.distinct.length == resultado.length)
  }
  //
  test("generarAsignaciones: 4 cursos y 2 aulas genera 16 asignaciones") {
    assert(generarAsignaciones(4,2).length == 16)
  }

  test("asignacionOptimaPar: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costo <= 37)
  }

}
