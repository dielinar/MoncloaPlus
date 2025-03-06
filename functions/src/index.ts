import * as functions from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import {google} from "googleapis";
import {getSheetsFormatting} from "./sheetsFormatting.js";
// import {getCorrectDate} from "./getCorrectDate.js";

// Inicializar Firebase Admin
admin.initializeApp();

// ID de tu Google Sheets
const SHEET_ID = "17cEPfdBHfoaLyUHxk1pE0XOEJPV_Stys99Bi7cDXyOI";

let sheetsClient: ReturnType<typeof google.sheets> | null = null;

/**
 * Cloud Function que exporta datos a Google Sheets.
 */
async function getSheetsClient() {
  if (sheetsClient) return sheetsClient;

  const credentialsBase64 = process.env.GOOGLE_CREDENTIALS_BASE64;
  if (!credentialsBase64) {
    throw new Error("Las credenciales de Google Sheets no están definidas.");
  }
  const credentials = JSON.parse(
    Buffer.from(credentialsBase64, "base64").toString()
  );

  const auth = new google.auth.GoogleAuth({
    credentials,
    scopes: ["https://www.googleapis.com/auth/spreadsheets"],
  });
  sheetsClient = google.sheets({version: "v4", auth});
  return sheetsClient;
}

export const exportMealsToSheets = functions.onRequest(
  {secrets: ["GOOGLE_CREDENTIALS_BASE64"]},
  async (req, res) => {
    try {
      const {date, day} = req.query;
      if (!date || !day) {
        res.status(400).send("Faltan parámetros: 'date' y 'day' son obligatorios.");
        return;
      }

      const db = admin.firestore();
      const usersSnapshot = await db.collection("users").get();
      const mealsMap: Record<string, Record<string, number>> = {
        Desayuno: {},
        Comida: {},
        Cena: {},
      };
      const userMeals = [];

      for (const userDoc of usersSnapshot.docs) {
        const userData = userDoc.data();
        const weekSnapshot = await userDoc.ref.collection("weeks").doc(date as string).get();

        if (weekSnapshot.exists) {
          const meals = weekSnapshot.data()?.meals || {};
          const dayMeals = meals[day as string] || {};
          const userName = `${userData.firstName || "Sin nombre"} ${userData.firstSurname || ""} ${userData.secondSurname || ""}`;

          userMeals.push({userName, ...dayMeals});

          Object.entries(dayMeals).forEach(([mealType, mealOption]) => {
            if (!(mealType in mealsMap)) return; // Verifica que mealType es válido
            if (typeof mealOption !== "string") return;

            const mealCategory = mealsMap[mealType]; // Accede al objeto correcto

            if (mealOption as string === "-" || mealOption as string === "X") {
              mealOption = "No apuntados";
            }

            if (!mealCategory[mealOption as string]) {
              mealCategory[mealOption as string] = 0;
            }
            mealCategory[mealOption as string]++;
          });
        }
      }

      const sheets = await getSheetsClient();
      const sheetTitle = `${date}-${day}`;

      // Crear una nueva hoja con el nombre "date-day" si no existe
      await sheets.spreadsheets.batchUpdate({
        spreadsheetId: SHEET_ID,
        requestBody: {
          requests: [
            {
              addSheet: {
                properties: {title: sheetTitle},
              },
            },
          ],
        },
      }).catch(() => {
        // Si la hoja ya existe, ignorar el error
      });

      const spreadsheet = await sheets.spreadsheets.get({spreadsheetId: SHEET_ID});
      const sheet = spreadsheet.data.sheets?.find((s) => s.properties?.title === sheetTitle);
      const sheetId = sheet?.properties?.sheetId || 0;

      const values = [[`${day}: semana ${date}`]];
      values.push(["", "", "Desayuno", "", "Comida", "", "", "Cena", "", ""]);
      values.push(["Nombre", "", "Pronto", "Normal", "Pronto", "Normal", "Tarde", "Normal", "Tarde"]);

      userMeals.sort((a, b) => a.userName.localeCompare(b.userName));

      userMeals.forEach(({userName, Desayuno = "", Comida = "", Cena = ""}) => {
        values.push([
          userName, "",
          Desayuno === "Pronto" ? "x" : "",
          Desayuno === "Normal" ? "x" : "",
          Comida === "Pronto" ? "x" : "",
          Comida === "Normal" ? "x" : "",
          Comida === "Tarde" ? "x" : "",
          Cena === "Normal" ? "x" : "",
          Cena === "Tarde" ? "x" : "",
        ]);
      });

      values.push(
        [""],
        ["Desayuno"],
        ["Pronto", String(mealsMap.Desayuno.Pronto || 0)],
        ["Normal", String(mealsMap.Desayuno.Normal || 0)],
        ["No apuntados", String(mealsMap.Desayuno["No apuntados"] || 0)],
      );

      values.push(
        [""],
        ["Comida"],
        ["Pronto", String(mealsMap.Comida.Pronto || 0)],
        ["Normal", String(mealsMap.Comida.Normal || 0)],
        ["Tarde", String(mealsMap.Comida.Tarde || 0)],
        ["No apuntados", String(mealsMap.Comida["No apuntados"] || 0)],
      );

      values.push(
        [""],
        ["Cena"],
        ["Normal", String(mealsMap.Cena.Normal || 0)],
        ["Tarde", String(mealsMap.Cena.Tarde || 0)],
        ["No apuntados", String(mealsMap.Cena["No apuntados"] || 0)],
      );

      // Escribir datos en la nueva hoja
      await sheets.spreadsheets.values.update({
        spreadsheetId: SHEET_ID,
        range: `${sheetTitle}!A1`,
        valueInputOption: "RAW",
        requestBody: {values},
      });

      const numUsers = userMeals.length;
      const requests = getSheetsFormatting(sheetId, numUsers);

      await sheets.spreadsheets.batchUpdate({
        spreadsheetId: SHEET_ID,
        requestBody: {requests},
      });

      const sheetUrl = `https://docs.google.com/spreadsheets/d/${SHEET_ID}/export?format=pdf&portrait=true&size=a4&gid=${sheetId}`;

      res.status(200).json({
        message: `Datos exportados para ${day} de la semana ${date}`,
        exportUrl: sheetUrl,
      });
    } catch (error) {
      console.error("Error exportando a Sheets:", error);
      res.status(500).send("Error interno");
    }
  }
);
