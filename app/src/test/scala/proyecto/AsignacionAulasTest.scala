package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasTest extends AnyFunSuite {

  // Ejemplo 1 del enunciado
  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40)) //30
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)
  val a: Asignacion = Vector(0,1,0)
  // solapan
  test("solapan: M01[4,8) y M02[6,10) se solapan") {
    assert(solapan(("M01", 4, 8, 25), ("M02", 6, 10, 30)))
  }

  test("solapan: M01[4,8) y M03[12,16) no se solapan") {
    assert(!solapan(("M01", 4, 8, 25), ("M03", 12, 16, 20)))
  }

  test("solapan: cursos adyacentes [0,4) y [4,8) no se solapan") {
    assert(!solapan(("A", 0, 4, 10), ("B", 4, 8, 10)))
  }

  test("solapan: mismo horario exacto [4,8) y [4,8) se solapan") {
    assert(solapan(("X", 4, 8, 20), ("Y", 4, 8, 20)))
  }

  test("solapan: un curso dentro de otro [2,10) y [4,6) se solapan") {
    assert(solapan(("X", 2, 10, 20), ("Y", 4, 6, 15)))
  }

  test("solapan: se tocan al final [0,6) y [6,10) no se solapan") {
    assert(!solapan(("X", 0, 6, 20), ("Y", 6, 10, 15)))
  }

  test("solapan: separados con brecha [0,3) y [5,8) no se solapan") {
    assert(!solapan(("X", 0, 3, 20), ("Y", 5, 8, 15)))
  }

  test("solapan: cruce parcial invertido [6,10) y [4,8) se solapan") {
    assert(solapan(("X", 6, 10, 20), ("Y", 4, 8, 15)))
  }

  // choques
  test("choques: asignacion [0,0,1] tiene 1 choque (M01 y M02 en E101)") {
    assert(choques(c1, Vector(0, 0, 1)) == 1)
  }

  test("choques: asignacion [0,1,0] no tiene choques") {
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  test("choques: todos en aulas distintas, no hay choques") {
    val cursos = Vector(("C1", 0, 4, 20), ("C2", 0, 4, 20), ("C3", 0, 4, 20))
    val asig   = Vector(0, 1, 2)
    assert(choques(cursos, asig) == 0)
  }

  test("choques: tres cursos en la misma aula y todos se solapan, hay 3 choques") {
    val cursos = Vector(("C1", 0, 6, 20), ("C2", 2, 8, 20), ("C3", 4, 10, 20))
    val asig   = Vector(0, 0, 0)
    assert(choques(cursos, asig) == 3)
  }

  test("choques: cursos sin asignar (-1) no cuentan como choque") {
    val cursos = Vector(("C1", 0, 6, 20), ("C2", 2, 8, 20))
    val asig   = Vector(-1, -1)
    assert(choques(cursos, asig) == 0)
  }

  test("choques: dos pares de choques en aulas distintas") {
    // C1 y C2 chocan en aula 0, C3 y C4 chocan en aula 1
    val cursos = Vector(("C1", 0, 6, 20), ("C2", 3, 9, 20), ("C3", 0, 6, 20), ("C4", 3, 9, 20))
    val asig   = Vector(0, 0, 1, 1)
    assert(choques(cursos, asig) == 2)
  }

  test("choques: misma aula pero no se solapan, no hay choque") {
    val cursos = Vector(("C1", 0, 4, 20), ("C2", 4, 8, 20), ("C3", 8, 12, 20))
    val asig   = Vector(0, 0, 0)
    assert(choques(cursos, asig) == 0)
  }

  // capacidadFallida
  test("capacidadFallida: asignacion [0,0,1] no falla capacidad") {
    assert(capacidadFallida(c1, a1, Vector(0, 0, 1)) == 0)
  }
  //
  test("capacidadFallida: cursos asignados a aulas con capacidad suficiente") {
    val cursos = Vector(
      ("A",0,2,50),
      ("B",2,4,20)
    )

    val aulas = Vector(
      ("E1",30),
      ("E2",60)
    )

    val a1 = Vector(1,0)
    val n = capacidadFallida(cursos,aulas,a1)
    assert(n == 0)
  }

  test("capacidadFallida: un curso falla") {
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )

    val cursos = Vector(
      ("C1",0,2,25),
      ("C2",2,4,35),
      ("C3",4,6,45),
      ("C4",6,8,55)
    )

    assert(
      capacidadFallida(
        cursos,
        aulas,
        Vector(0,1,2,2)
      ) == 1
    )
  }

  test("capacidadFallida: dos cursos fallan") {
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )

    val cursos = Vector(
      ("C1",0,2,25),
      ("C2",2,4,35),
      ("C3",4,6,45),
      ("C4",6,8,55)
    )

    assert(
      capacidadFallida(
        cursos,
        aulas,
        Vector(0,0,2,2)
      ) == 2
    )
  }

  test("capacidadFallida: todos los cursos fallan") {
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )

    val cursos = Vector(
      ("C1",0,2,60),
      ("C2",2,4,70),
      ("C3",4,6,80)
    )

    assert(
      capacidadFallida(
        cursos,
        aulas,
        Vector(0,1,2)
      ) == 3
    )
  }

  test("capacidadFallida: varios cursos comparten la misma aula") {
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )

    val cursos = Vector(
      ("C1",0,2,25),
      ("C2",2,4,35),
      ("C3",4,6,45),
      ("C4",6,8,55)
    )

    assert(
      capacidadFallida(
        cursos,
        aulas,
        Vector(0,0,0,0)
      ) == 3
    )
  }

  // desperdicio
  test("desperdicio: asignacion [0,0,1] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E101(30)-M02(30)=0, E102(40)-M03(20)=20 → 25
    assert(desperdicio(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("desperdicio: asignacion [0,1,0] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E102(40)-M02(30)=10, E101(30)-M03(20)=10 → 25
    assert(desperdicio(c1, a1, Vector(0, 1, 0)) == 25)
  }

  //
  test("desperdicio: capacidad exacta produce desperdicio 0") {
    val cursos = Vector(
      ("A",0,2,30),
      ("B",2,4,40),
      ("C",4,6,50)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )
    assert(desperdicio(cursos,aulas,Vector(0,1,2)) == 0)
  }

  test("desperdicio: todos los cursos caben y generan desperdicio") {
    val cursos = Vector(
      ("A",0,2,10),
      ("B",2,4,20),
      ("C",4,6,30)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",40),
      ("E3",50)
    )
    assert(desperdicio(cursos,aulas,Vector(0,1,2)) == 60)
  }

  test("desperdicio: cursos sin capacidad suficiente no aportan desperdicio") {
    val cursos = Vector(
      ("A",0,2,35),
      ("B",2,4,20)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",40)
    )
    assert(desperdicio(cursos,aulas,Vector(0,1)) == 20)
  }

  test("desperdicio: varios cursos comparten la misma aula") {
    val cursos = Vector(
      ("A",0,2,20),
      ("B",2,4,25),
      ("C",4,6,30)
    )
    val aulas = Vector(
      ("E1",40)
    )
    assert(desperdicio(cursos,aulas,Vector(0,0,0)) == 45)
  }

  test("desperdicio: la asignacion determina el aula usada") {
    val cursos = Vector(
      ("A",0,2,50),
      ("B",2,4,20)
    )
    val aulas = Vector(
      ("E1",30),
      ("E2",60)
    )
    assert(desperdicio(cursos,aulas,Vector(1,0)) == 20)
  }

  // costoAsignacion
  test("costoAsignacion: asignacion [0,0,1] cuesta 1031") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 0, 1), w) == 1031)
  }

  test("costoAsignacion: asignacion [0,1,0] cuesta 37") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 1, 0), w) == 37)
  }

  // generarAsignaciones
  test("generarAsignaciones: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignaciones(2, 2).length == 4)
  }

  test("generarAsignaciones: 3 cursos y 3 aulas produce 27 asignaciones") {
    assert(generarAsignaciones(3, 3).length == 27)
  }

  //
  test("generarAsignaciones: 0 cursos genera una asignacion vacia caso extremo") {
    assert(
      generarAsignaciones(0,2) == Vector(Vector()))
  }

  test("generarAsignaciones: 2 cursos y 3 aulas generan 9 asignaciones") {
    assert(
      generarAsignaciones(2,3).length == 9
    )
  }

  test("generarAsignaciones: 3 cursos y 2 aulas generan 8 asignaciones") {
    assert(
      generarAsignaciones(3,2).length == 8
    )
  }

  test("generarAsignaciones: contiene la asignacion Vector(1,1)") {
    val resultado = generarAsignaciones(2,2)
    assert(resultado.contains(Vector(1,1)))
  }

  test("generarAsignaciones: contiene todas las asignaciones para 1 curso y 3 aulas caso extremo") {
    assert(
      generarAsignaciones(1,3) ==
        Vector(
          Vector(0),
          Vector(1),
          Vector(2)
        ))
  }
  // asignacionOptima
  test("asignacionOptima: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptima(c1, a1, d1, w)
    assert(costo <= 37)
  }

  
}
