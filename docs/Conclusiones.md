# Conclusiones

**Integrantes:** 
Yonier Alejandro Vega Rojas	2477056	
Andres Felipe Quiceno Gil	2477362	
Juan Esteban Martinez Orobio	2569452	
---

## Conclusiones del proyecto

1. **Programación funcional:**

La programacion Funcional nos permitio cambiar un poco nuestra forma de programar ya que pasamos de pensar en cosas iterativas a pensar de forma mas de expresiones,condiciones y reglas con la ayuda de la recursion y funciones de alto orden como map,filter,flatMap,sum,count etc. Esto nos ayudo a expresar tranformaciones sobre colecciones de forma precisa y eficiente evitando los estados mutables.Otra ventaja es que nos permitio entender un poco el detras de como funcionan muchas estructuras que solemos utilizar a la hora de programar ejemplo el funcionamiento de los join de SQL simulado con expresiones for.La dificultad que presentamos tiene que ver un poco con el cambio de mentalidad al programar en el tema de resolver problemas. Al inicio se nos dificulto la recursion principalmente pero con practica y entenderla mejor pudimos avanzar y mejorar adecuadamente.

2. **Corrección:** 
La corrección de las implementaciones se argumentó mediante razonamiento inductivo sobre la estructura de los datos y la recursión utilizada. En funciones recursivas se rectifico que el caso base generara un resultado correcto y que el paso recursivo preservara la propiedad deseada sin fallar. Ademas , se crearon pruebas unitarias que cubrieron casos normales, casos extremos y situaciones límite para verificar el comportamiento esperado de las funciones y rectificar que funcionaran adecuadamente. En las funciones de conteo y agregación se utilizaron invariantes relacionados con la correcta asociación entre cursos, aulas y asignaciones.

3. **Paralelismo:**
La paralelización resultó beneficiosa principalmente en problemas donde el trabajo podía dividirse en partes independientes especificamente cuando el trabajo es muy grande en cuanto a datos, como el cálculo de choques, desperdicio, movilidad y la búsqueda de la asignación óptima. En instancias pequeñas, la creación y sincronización de tareas paralelas introdujo una sobrecarga que en algunos casos hizo que la versión secuencial fuera igual o incluso más rápida. No obstante, a medida que aumentó el tamaño del espacio de búsqueda, especificamente en la generación y evaluación de asignaciones, el paralelismo permitió optimizar significativamente los tiempos de ejecución. Esto evidencia que el beneficio del paralelismo depende del tamaño del problema y del costo relativo de coordinación entre tareas para evitar a toda costa el overhead.

4. **Aprendizajes:**
Los conceptos más útiles del curso fueron la recursión, las funciones de alto orden como map,filter, el reconocimiento de patrones para deconstruir una estructura como una lista y las técnicas de paralelización mediante división del trabajo usando parallel. También fue importante comprender cómo modelar un problema mediante tipos de datos inmutables y operaciones sobre colecciones manejando expresiones de predicados como por ejemplo p=>p>1. Si se volviera a desarrollar el proyecto desde el inicio, sería conveniente planear con mayor detalle las estructuras auxiliares y los casos de prueba antes de implementar las funciones adecuadamente, así como diseñar desde el inicio una estrategia clara y contundente para las versiones paralelas y para la realizacion de la medición de rendimiento con scala meter.





