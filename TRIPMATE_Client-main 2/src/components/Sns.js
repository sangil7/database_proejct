import React from "react";

const SNS = () => {
  return (
    <div className="flex">
      {/* Sidebar */}
      <div className="w-1/5 bg-white h-screen shadow-md">
        <div className="p-4 text-center">
          <h1 className="text-lg font-bold">VOYAGE CONNECT</h1>
        </div>
        <nav className="mt-10">
          <ul>
            <li className="flex items-center p-4 hover:bg-gray-200">
              <span className="material-icons">star</span>
              <span className="ml-2">Story</span>
            </li>
            <li className="flex items-center p-4 hover:bg-gray-200">
              <span className="material-icons">chat</span>
              <span className="ml-2">Chat</span>
            </li>
            <li className="flex items-center p-4 hover:bg-gray-200">
              <span className="material-icons">person</span>
              <span className="ml-2">Profile</span>
            </li>
            <li className="flex items-center p-4 hover:bg-gray-200">
              <span className="material-icons">settings</span>
              <span className="ml-2">Setting</span>
            </li>
          </ul>
        </nav>
      </div>

      {/* Main Content */}
      <div className="w-4/5 p-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold">SNS Feed</h2>
        </div>
        <div className="space-y-8">
          {/* Dummy Post 1 */}
          <div className="bg-white p-4 rounded-lg shadow-md">
            <div className="flex items-center mb-4">
              <img
                src="https://via.placeholder.com/40"
                alt="Profile"
                className="rounded-full"
              />
              <span className="ml-2 font-bold">user_one</span>
            </div>
            <div className="grid grid-cols-3 gap-4">
              <img
                src="https://source.unsplash.com/random/300x200?city"
                alt="Post Image"
                className="rounded-lg"
              />
              <img
                src="https://source.unsplash.com/random/300x200?nature"
                alt="Post Image"
                className="rounded-lg"
              />
              <img
                src="https://source.unsplash.com/random/300x200?travel"
                alt="Post Image"
                className="rounded-lg"
              />
            </div>
            <div className="flex items-center mt-4">
              <span className="material-icons">favorite_border</span>
              <span className="ml-2">Location</span>
            </div>
          </div>

          {/* Dummy Post 2 */}
          <div className="bg-white p-4 rounded-lg shadow-md">
            <div className="flex items-center mb-4">
              <img
                src="https://via.placeholder.com/40"
                alt="Profile"
                className="rounded-full"
              />
              <span className="ml-2 font-bold">user_two</span>
            </div>
            <div className="grid grid-cols-3 gap-4">
              <img
                src="https://source.unsplash.com/random/300x200?beach"
                alt="Post Image"
                className="rounded-lg"
              />
              <img
                src="https://source.unsplash.com/random/300x200?food"
                alt="Post Image"
                className="rounded-lg"
              />
              <img
                src="https://source.unsplash.com/random/300x200?drink"
                alt="Post Image"
                className="rounded-lg"
              />
            </div>
            <div className="flex items-center mt-4">
              <span className="material-icons">favorite_border</span>
              <span className="ml-2">Beach</span>
            </div>
          </div>

          {/* Dummy Post 3 */}
          <div className="bg-white p-4 rounded-lg shadow-md">
            <div className="flex items-center mb-4">
              <img
                src="https://via.placeholder.com/40"
                alt="Profile"
                className="rounded-full"
              />
              <span className="ml-2 font-bold">user_three</span>
            </div>
            <div className="grid grid-cols-3 gap-4">
              <img
                src="https://source.unsplash.com/random/300x200?food"
                alt="Post Image"
                className="rounded-lg"
              />
              <img
                src="https://source.unsplash.com/random/300x200?sunset"
                alt="Post Image"
                className="rounded-lg"
              />
              <img
                src="https://source.unsplash.com/random/300x200?mountain"
                alt="Post Image"
                className="rounded-lg"
              />
            </div>
            <div className="flex items-center mt-4">
              <span className="material-icons">favorite_border</span>
              <span className="ml-2">Adventure</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SNS;
