import React, { useEffect, useState } from "react";
import {
  GoogleMap,
  Marker,
  InfoWindow,
  useJsApiLoader,
} from "@react-google-maps/api";
import axios from "axios";

const RecTab = () => {
  const [map, setMap] = useState(null);
  const [recommendations, setRecommendations] = useState([]);
  const [error, setError] = useState("");
  const [userLocation, setUserLocation] = useState(null);
  const [loadingLocation, setLoadingLocation] = useState(true);
  const [loadingRecommendations, setLoadingRecommendations] = useState(false);
  const [selectedLocation, setSelectedLocation] = useState(null);

  const { isLoaded } = useJsApiLoader({
    googleMapsApiKey: "AIzaSyCNH8xLS_XvX0pVVSYtBNjCUxc50iwgb20",
    libraries: ["places"],
  });

  useEffect(() => {
    const fetchUserLocation = () => {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            const { latitude, longitude } = position.coords;
            setUserLocation({
              latitude: parseFloat(latitude),
              longitude: parseFloat(longitude),
            });
            setLoadingLocation(false);
          },
          (err) => {
            setError(
              "Failed to retrieve location. Please enable location services."
            );
            setLoadingLocation(false);
            console.error(err);
          }
        );
      } else {
        setError("Geolocation is not supported by this browser.");
        setLoadingLocation(false);
      }
    };

    fetchUserLocation();
  }, []);

  useEffect(() => {
    const fetchRecommendations = async () => {
      if (userLocation) {
        setLoadingRecommendations(true);
        setError("");
        try {
          const userId = "defaultUser";
          const response = await axios.post("http://54.180.134.119:5000/recommend", {
            user_id: userId,
            latitude: userLocation.latitude,
            longitude: userLocation.longitude,
          });

          const recommendations = response.data.results
            .slice(0, 5)
            .map((location) => ({
              ...location,
              latitude: parseFloat(location.latitude),
              longitude: parseFloat(location.longitude),
            }));

          setRecommendations(recommendations);
        } catch (err) {
          setError(
            "Failed to load recommended locations. Please try again later."
          );
          console.error(err);
        } finally {
          setLoadingRecommendations(false);
        }
      }
    };

    // // 더미 데이터 부분 , 실제로는 위에 코드 사용해야 함
    // useEffect(() => {
    //   const fetchUserLocation = () => {
    //     // 더미 사용자 위치
    //     const dummyLocation = { latitude: 37.5665, longitude: 126.978 };
    //     setUserLocation(dummyLocation);
    //     setLoadingLocation(false);
    //   };

    //   fetchUserLocation();
    // }, []);

    // useEffect(() => {
    //   const fetchRecommendations = async () => {
    //     if (userLocation) {
    //       setLoadingRecommendations(true);
    //       setError("");

    //       // 더미 추천 장소 데이터
    //       const dummyRecommendations = [
    //         {
    //           name: "서울 연남동",
    //           description: "트렌디한 카페와 맛집이 많은 곳",
    //           latitude: 37.5645,
    //           longitude: 126.935,
    //           distance: 1.2,
    //           type: "Cafe",
    //         },
    //         {
    //           name: "서울 연희동",
    //           description: "아기자기한 가게들이 즐비한 거리",
    //           latitude: 37.5635,
    //           longitude: 126.934,
    //           distance: 1.6,
    //           type: "Shop",
    //         },
    //         {
    //           name: "서울 합정동",
    //           description: "힙한 분위기의 바와 레스토랑",
    //           latitude: 37.549,
    //           longitude: 126.911,
    //           distance: 2.1,
    //           type: "Bar",
    //         },
    //         {
    //           name: "서울 망원동",
    //           description: "한적한 공원과 마켓이 있는 동네",
    //           latitude: 37.5535,
    //           longitude: 126.913,
    //           distance: 2.8,
    //           type: "Market",
    //         },
    //         {
    //           name: "서울 홍대",
    //           description: "젊은이들이 모이는 핫플레이스",
    //           latitude: 37.5572,
    //           longitude: 126.9256,
    //           distance: 3.3,
    //           type: "Entertainment",
    //         },
    //       ];

    //       setRecommendations(dummyRecommendations);
    //       setLoadingRecommendations(false);
    //     }
    //   };

    //   // 더미 데이터 부분

    fetchRecommendations();
  }, [userLocation]);

  return isLoaded ? (
    <div className="flex">
      {/* Sidebar */}
      <div
        style={{ width: "120px" }}
        className="bg-white shadow-md h-screen flex flex-col items-center py-4"
      >
        <button
          style={{ width: "50px", height: "50px", padding: "4px" }}
          onClick={() => (window.location.href = "http://localhost:3000/Home")}
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            style={{ width: "40px", height: "40px" }}
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M15 19l-7-7 7-7"
            />
          </svg>
        </button>

        <div className="my-10 border-b border-gray-300"></div>

        {/* 나머지 버튼들 */}
        <div className="flex flex-col items-center">
          {/* Star Icon */}
          <button
            className="mb-12 flex flex-col items-center justify-center"
            style={{ width: "60px", height: "60px" }}
            onClick={() => (window.location.href = "http://localhost:3000/Sns")}
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M12 17.27L18.18 21 16.54 13.97 22 9.24 14.81 8.63 12 2 9.19 8.63 2 9.24 7.46 13.97 5.82 21 12 17.27z"
              />
            </svg>
            <span className="text-xs">Story</span>
          </button>

          {/* Chat Icon */}
          <button
            className="mb-12 flex flex-col items-center justify-center"
            style={{ width: "60px", height: "60px" }}
            onClick={() =>
              (window.location.href = "http://localhost:3000/Chat")
            }
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M8 12h8m-4 4h4m-4-8h4m-8 4h.01M4 4h16v16H4V4z"
              />
            </svg>
            <span className="text-xs">Chat</span>
          </button>

          {/* Profile */}
          <button
            className="mb-12 flex flex-col items-center justify-center"
            style={{ width: "60px", height: "60px" }}
            onClick={() =>
              (window.location.href = "http://localhost:3000/Profile")
            }
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M12 14c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-3.33 0-10 1.67-10 5v1h20v-1c0-3.33-6.67-5-10-5z"
              />
            </svg>
            <span className="text-xs">Profile</span>
          </button>

          {/* Settings */}
          <button
            className="flex flex-col items-center justify-center"
            style={{ width: "60px", height: "60px" }}
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M12 8.5v3.5m0 0v3.5m0-3.5h3.5m-3.5 0H8.5m8.5 0a1 1 0 011 1v0a1 1 0 01-.25.67l-2.5 2.5a1 1 0 01-.67.25h-0a1 1 0 01-1-1v-3.5a1 1 0 011-1h0zm-8.5 0a1 1 0 00-1 1v0a1 1 0 00.25.67l2.5 2.5a1 1 0 00.67.25h0a1 1 0 001-1v-3.5a1 1 0 00-1-1h0z"
              />
            </svg>
            <span className="text-xs">Setting</span>
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 p-6">
        <h1 className="text-4xl font-bold mb-4">AI Service</h1>
        {error && <p className="text-red-500">{error}</p>}

        <button className="absolute top-4 right-6 w-8 h-8 p-1.5 rounded-full bg-black-200 hover:bg-gray-300">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
            />
          </svg>
        </button>
        {loadingLocation ? (
          <p>Loading user location...</p>
        ) : userLocation ? (
          <>
            <h2 className="text-xl font-semibold mb-4">
              📌 Recommended Locations
            </h2>
            <GoogleMap
              mapContainerStyle={{ height: "500px", width: "100%" }}
              center={{
                lat: userLocation.latitude,
                lng: userLocation.longitude,
              }}
              zoom={13}
              onLoad={(mapInstance) => setMap(mapInstance)}
            >
              <Marker
                position={{
                  lat: userLocation.latitude,
                  lng: userLocation.longitude,
                }}
                icon={{
                  url: "http://maps.google.com/mapfiles/ms/icons/blue-dot.png",
                }}
                onClick={() =>
                  setSelectedLocation({
                    name: "Your Location",
                    description: "You are here",
                    latitude: userLocation.latitude,
                    longitude: userLocation.longitude,
                  })
                }
              />

              {recommendations.map((location, index) => (
                <Marker
                  key={index}
                  position={{
                    lat: location.latitude,
                    lng: location.longitude,
                  }}
                  onClick={() => setSelectedLocation(location)}
                />
              ))}

              {selectedLocation && (
                <InfoWindow
                  position={{
                    lat: selectedLocation.latitude,
                    lng: selectedLocation.longitude,
                  }}
                  onCloseClick={() => setSelectedLocation(null)}
                >
                  <div>
                    <h4>{selectedLocation.name}</h4>
                    <p>{selectedLocation.description}</p>
                  </div>
                </InfoWindow>
              )}
            </GoogleMap>

            {/* 추천 장소 목록 표시 */}
            <div className="bg-white p-4 rounded-lg shadow-md mt-4">
              <h3 className="text-xl font-bold mb-2">
                📌 Nearby Recommendations
              </h3>
              {loadingRecommendations ? (
                <p>Loading recommendations...</p>
              ) : recommendations.length > 0 ? (
                recommendations.map((location, index) => (
                  <div
                    key={index}
                    className="bg-blue-100 p-4 rounded-lg flex justify-between items-center mb-2"
                  >
                    <div>
                      <h4 className="font-semibold">{location.name}</h4>
                      <p>{location.description}</p>
                      <p>
                        Distance:{" "}
                        {location.distance
                          ? location.distance.toFixed(2) + " km"
                          : "N/A"}
                      </p>
                      <p>Type: {location.type}</p>
                    </div>
                  </div>
                ))
              ) : (
                <p>No recommendations available.</p>
              )}
            </div>
          </>
        ) : (
          <p>Location not available.</p>
        )}
      </div>
    </div>
  ) : (
    <p>Loading...</p>
  );
};

export default RecTab;
