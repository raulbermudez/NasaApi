# API de la nasa

## Objetivo
Crear un endpoint que nos devolviera los 3 asteroides con mayor diametro que tengan riesgo de impacto entre una fecha determinada, el usuario solo deberá especificar el numero de dias en la ruta y nosotros nos encargaremos de hacerle una petición a la api de la nasa, filtrar y devolver los siguientes datos:

- Nombre
- Velocidad
- Diametro medio
- Planeta donde impactara
- Fecha

Para poder recibir esa información hemos buscado desde la fecha del dia en que se hace la petición hasta el número de días introducido en la ruta, que nunca superará los 7 días, ni podrá ser inferior a 1.

## Nuevas implementaciones
1. Una vez finalizada la parte de los objetivos, he añadido un nuevo endpoint "/foto" que se encarga de pedir a otra api de la nasa la foto/video del dia actual y devuelvo la siguiente información:

    - Fecha
    - Explicación de la foto
    - Titulo
    - Url
