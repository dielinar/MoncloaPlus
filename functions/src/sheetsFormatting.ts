/**
 * Genera las configuraciones de formato para una hoja de Google Sheets.
 * @param {number} sheetId - ID de la hoja de cálculo.
 * @param {number} numUsers - Número de usuarios en la hoja.
 * @return {Object[]} Un array con los requests de formateo.
 */
export function getSheetsFormatting(sheetId: number, numUsers: number) {
  const usersEndRow = 3 + numUsers;
  const desayunoStartRow = usersEndRow + 1;
  const desayunoEndRow = desayunoStartRow + 4;
  const comidaStartRow = desayunoEndRow + 1;
  const comidaEndRow = comidaStartRow + 5;
  const cenaStartRow = comidaEndRow + 1;
  const cenaEndRow = cenaStartRow + 4;

  return [
    {
      updateSheetProperties: {
        properties: {
          sheetId,
          gridProperties: {
            hideGridlines: true,
          },
        },
        fields: "gridProperties.hideGridlines",
      },
    },
    {
      updateBorders: { // Bordes tabla principal de usuarios
        range: {
          sheetId,
          startRowIndex: 0,
          endRowIndex: usersEndRow,
          startColumnIndex: 0,
          endColumnIndex: 9,
        },
        top: {style: "SOLID_MEDIUM"},
        bottom: {style: "SOLID_MEDIUM"},
        left: {style: "SOLID_MEDIUM"},
        right: {style: "SOLID_MEDIUM"},
        innerHorizontal: {style: "SOLID"},
        innerVertical: {style: "SOLID"},
      },
    },
    {
      updateBorders: { // Doble borde Desayuno
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: usersEndRow,
          startColumnIndex: 2,
          endColumnIndex: 4,
        },
        left: {style: "SOLID_MEDIUM"},
        right: {style: "SOLID_MEDIUM"},
      },
    },
    {
      updateBorders: { // Encabezado Desayuno
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: 2,
          startColumnIndex: 2,
          endColumnIndex: 4,
        },
        top: {style: "SOLID_MEDIUM"},
        bottom: {style: "SOLID_MEDIUM"},
        right: {style: "SOLID_MEDIUM"},
      },
    },
    {
      updateBorders: { // Doble borde Comida
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: usersEndRow,
          startColumnIndex: 4,
          endColumnIndex: 7,
        },
        right: {style: "SOLID_MEDIUM"},
      },
    },
    {
      updateBorders: { // Encabezado Comida
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: 2,
          startColumnIndex: 4,
          endColumnIndex: 7,
        },
        top: {style: "SOLID_MEDIUM"},
        bottom: {style: "SOLID_MEDIUM"},
        right: {style: "SOLID_MEDIUM"},
      },
    },
    {
      updateBorders: { // Bordes Cena
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: 2,
          startColumnIndex: 7,
          endColumnIndex: 9,
        },
        top: {style: "SOLID_MEDIUM"},
        bottom: {style: "SOLID_MEDIUM"},
      },
    },
    {
      mergeCells: { // Combinar celdas para la fila de la fecha
        range: {
          sheetId,
          startRowIndex: 0,
          endRowIndex: 1,
          startColumnIndex: 0,
          endColumnIndex: 9,
        },
        mergeType: "MERGE_ALL",
      },
    },
    {
      repeatCell: { // Formato para la fecha
        range: {
          sheetId,
          startRowIndex: 0,
          endRowIndex: 1,
          startColumnIndex: 0,
          endColumnIndex: 9,
        },
        cell: {
          userEnteredFormat: {
            horizontalAlignment: "CENTER",
            textFormat: {
              bold: true,
              fontSize: 13,
            },
          },
        },
        fields: "userEnteredFormat(horizontalAlignment,textFormat)",
      },
    },
    {
      mergeCells: { // Combinar celdas para Desayuno
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: 2,
          startColumnIndex: 2,
          endColumnIndex: 4,
        },
        mergeType: "MERGE_ALL",
      },
    },
    {
      repeatCell: { // Formato para Desayuno
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: 2,
          startColumnIndex: 2,
          endColumnIndex: 4,
        },
        cell: {
          userEnteredFormat: {
            backgroundColor: {
              red: 0.839,
              green: 0.965,
              blue: 0.839,
            },
            horizontalAlignment: "CENTER",
            textFormat: {
              bold: true,
              fontSize: 12,
            },
          },
        },
        fields: "userEnteredFormat(backgroundColor,horizontalAlignment,textFormat)",
      },
    },
    {
      mergeCells: { // Combinar celdas para Comida
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: 2,
          startColumnIndex: 4,
          endColumnIndex: 7,
        },
        mergeType: "MERGE_ALL",
      },
    },
    {
      repeatCell: { // Formato para Comida
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: 2,
          startColumnIndex: 4,
          endColumnIndex: 7,
        },
        cell: {
          userEnteredFormat: {
            backgroundColor: {
              red: 0.847,
              green: 0.886,
              blue: 1.0,
            },
            horizontalAlignment: "CENTER",
            textFormat: {
              bold: true,
              fontSize: 12,
            },
          },
        },
        fields: "userEnteredFormat(backgroundColor,horizontalAlignment,textFormat)",
      },
    },
    {
      mergeCells: { // Combinar celdas para Cena
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: 2,
          startColumnIndex: 7,
          endColumnIndex: 9,
        },
        mergeType: "MERGE_ALL",
      },
    },
    {
      repeatCell: { // Formato para Cena
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: 2,
          startColumnIndex: 7,
          endColumnIndex: 9,
        },
        cell: {
          userEnteredFormat: {
            backgroundColor: {
              red: 0.98,
              green: 0.843,
              blue: 0.729,
            },
            horizontalAlignment: "CENTER",
            textFormat: {
              bold: true,
              fontSize: 12,
            },
          },
        },
        fields: "userEnteredFormat(backgroundColor,horizontalAlignment,textFormat)",
      },
    },
    {
      repeatCell: { // Encabezados de nombre y turnos
        range: {
          sheetId,
          startRowIndex: 2,
          endRowIndex: 3,
          startColumnIndex: 0,
          endColumnIndex: 9,
        },
        cell: {
          userEnteredFormat: {
            horizontalAlignment: "CENTER",
            textFormat: {
              bold: true,
            },
          },
        },
        fields: "userEnteredFormat(horizontalAlignment,textFormat)",
      },
    },
    {
      updateBorders: { // Bordes nombre
        range: {
          sheetId,
          startRowIndex: 2,
          endRowIndex: 3,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        top: {style: "SOLID_MEDIUM"},
        bottom: {style: "SOLID_MEDIUM"},
      },
    },
    {
      mergeCells: {
        range: {
          sheetId,
          startRowIndex: 2,
          endRowIndex: 3,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        mergeType: "MERGE_ALL",
      },
    },
    {
      mergeCells: {
        range: {
          sheetId,
          startRowIndex: 1,
          endRowIndex: 2,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        mergeType: "MERGE_ALL",
      },
    },
    {
      repeatCell: { // Gris nombre
        range: {
          sheetId,
          startRowIndex: 2,
          endRowIndex: 3,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        cell: {
          userEnteredFormat: {
            backgroundColor: {
              red: 0.871,
              green: 0.871,
              blue: 0.863,
            },
          },
        },
        fields: "userEnteredFormat(backgroundColor)",
      },
    },
    {
      mergeCells: { // Combinar celdas para conteo de Desayuno
        range: {
          sheetId,
          startRowIndex: desayunoStartRow,
          endRowIndex: desayunoStartRow + 1,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        mergeType: "MERGE_ALL",
      },
    },
    {
      repeatCell: { // Formato encabezado conteo Desayuno
        range: {
          sheetId,
          startRowIndex: desayunoStartRow,
          endRowIndex: desayunoStartRow + 1,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        cell: {
          userEnteredFormat: {
            backgroundColor: {
              red: 0.839,
              green: 0.965,
              blue: 0.839,
            },
            horizontalAlignment: "CENTER",
            textFormat: {
              bold: true,
            },
          },
        },
        fields: "userEnteredFormat(backgroundColor,horizontalAlignment,textFormat)",
      },
    },
    {
      updateBorders: { // Bordes tabla conteo Desayuno
        range: {
          sheetId,
          startRowIndex: desayunoStartRow,
          endRowIndex: desayunoEndRow,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        top: {style: "SOLID_MEDIUM"},
        bottom: {style: "SOLID_MEDIUM"},
        left: {style: "SOLID_MEDIUM"},
        right: {style: "SOLID_MEDIUM"},
        innerHorizontal: {style: "SOLID"},
        innerVertical: {style: "SOLID"},
      },
    },
    {
      updateBorders: { // Bordes encabezado conteo Desayuno
        range: {
          sheetId,
          startRowIndex: desayunoStartRow,
          endRowIndex: desayunoStartRow + 1,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        bottom: {style: "SOLID_MEDIUM"},
      },
    },
    {
      repeatCell: { // Para que el conteo de los desayunos esté centrado
        range: {
          sheetId,
          startRowIndex: desayunoStartRow + 1,
          endRowIndex: desayunoEndRow,
          startColumnIndex: 1,
          endColumnIndex: 2,
        },
        cell: {
          userEnteredFormat: {
            horizontalAlignment: "CENTER",
          },
        },
        fields: "userEnteredFormat(horizontalAlignment)",
      },
    },
    {
      mergeCells: {
        range: {
          sheetId,
          startRowIndex: comidaStartRow,
          endRowIndex: comidaStartRow + 1,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        mergeType: "MERGE_ALL",
      },
    },
    {
      repeatCell: { // Formato encabezado conteo Comida
        range: {
          sheetId,
          startRowIndex: comidaStartRow,
          endRowIndex: comidaStartRow + 1,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        cell: {
          userEnteredFormat: {
            backgroundColor: {
              red: 0.847,
              green: 0.886,
              blue: 1.0,
            },
            horizontalAlignment: "CENTER",
            textFormat: {
              bold: true,
            },
          },
        },
        fields: "userEnteredFormat(backgroundColor,horizontalAlignment,textFormat)",
      },
    },
    {
      updateBorders: { // Bordes tabla conteo Comida
        range: {
          sheetId,
          startRowIndex: comidaStartRow,
          endRowIndex: comidaEndRow,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        top: {style: "SOLID_MEDIUM"},
        bottom: {style: "SOLID_MEDIUM"},
        left: {style: "SOLID_MEDIUM"},
        right: {style: "SOLID_MEDIUM"},
        innerHorizontal: {style: "SOLID"},
        innerVertical: {style: "SOLID"},
      },
    },
    {
      updateBorders: { // Bordes encabezado conteo Comida
        range: {
          sheetId,
          startRowIndex: comidaStartRow,
          endRowIndex: comidaStartRow + 1,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        bottom: {style: "SOLID_MEDIUM"},
      },
    },
    {
      repeatCell: { // Para que el conteo de las comidas esté centrado
        range: {
          sheetId,
          startRowIndex: comidaStartRow + 1,
          endRowIndex: comidaEndRow,
          startColumnIndex: 1,
          endColumnIndex: 2,
        },
        cell: {
          userEnteredFormat: {
            horizontalAlignment: "CENTER",
          },
        },
        fields: "userEnteredFormat(horizontalAlignment)",
      },
    },
    {
      mergeCells: {
        range: {
          sheetId,
          startRowIndex: cenaStartRow,
          endRowIndex: cenaStartRow + 1,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        mergeType: "MERGE_ALL",
      },
    },
    {
      repeatCell: { // Formato encabezado conteo Cena
        range: {
          sheetId,
          startRowIndex: cenaStartRow,
          endRowIndex: cenaStartRow + 1,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        cell: {
          userEnteredFormat: {
            backgroundColor: {
              red: 0.98,
              green: 0.843,
              blue: 0.729,
            },
            horizontalAlignment: "CENTER",
            textFormat: {
              bold: true,
            },
          },
        },
        fields: "userEnteredFormat(backgroundColor,horizontalAlignment,textFormat)",
      },
    },
    {
      updateBorders: { // Bordes tabla conteo Cena
        range: {
          sheetId,
          startRowIndex: cenaStartRow,
          endRowIndex: cenaEndRow,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        top: {style: "SOLID_MEDIUM"},
        bottom: {style: "SOLID_MEDIUM"},
        left: {style: "SOLID_MEDIUM"},
        right: {style: "SOLID_MEDIUM"},
        innerHorizontal: {style: "SOLID"},
        innerVertical: {style: "SOLID"},
      },
    },
    {
      updateBorders: { // Bordes encabezado conteo Cena
        range: {
          sheetId,
          startRowIndex: cenaStartRow,
          endRowIndex: cenaStartRow + 1,
          startColumnIndex: 0,
          endColumnIndex: 2,
        },
        bottom: {style: "SOLID_MEDIUM"},
      },
    },
    {
      repeatCell: { // Para que el conteo de las cenas esté centrado
        range: {
          sheetId,
          startRowIndex: cenaStartRow + 1,
          endRowIndex: cenaEndRow,
          startColumnIndex: 1,
          endColumnIndex: 2,
        },
        cell: {
          userEnteredFormat: {
            horizontalAlignment: "CENTER",
          },
        },
        fields: "userEnteredFormat(horizontalAlignment)",
      },
    },
    {
      repeatCell: { // Para que las x estén centradas
        range: {
          sheetId,
          startRowIndex: 3,
          endRowIndex: usersEndRow,
          startColumnIndex: 2,
          endColumnIndex: 9,
        },
        cell: {
          userEnteredFormat: {
            horizontalAlignment: "CENTER",
          },
        },
        fields: "userEnteredFormat(horizontalAlignment)",
      },
    },
  ];
}
