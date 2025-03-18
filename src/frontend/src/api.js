import axios from "axios";

const API_URL = "http://localhost:3000/api/flights"; 

export const getFlights = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error("Error fetching flights:", error);
    return [];
  }
};
