import axios from "axios";
//siin fetchime info backendist
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
export const getSeatsByLennuk = async (lennuk) => {
  const response = await fetch(`/api/istmed/${lennuk}`);
  const data = await response.json();
  return data;
};
