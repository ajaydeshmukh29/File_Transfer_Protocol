import { useState, useEffect } from "react";

import Login from "./components/Login";
import Register from "./components/Register";
import Header from "./components/Header";
import FileList from "./components/FileList";
import FileUpload from "./components/FileUpload";

import "./App.css";

function App()
{
  const [page, setPage] = useState("login");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [token, setToken] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);

  const [files, setFiles] = useState([]);


  // =====================================================
  // AUTH HEADER HELPER
  // =====================================================
  // Every /api/files/** call now requires a valid JWT
  // (see SecurityConfig on the backend), so this attaches
  // "Authorization: Bearer <token>" to every request below.

  const authHeaders = (extra = {}) => ({
    ...extra,
    Authorization: `Bearer ${token}`
  });


  // =====================================================
  // LOGIN
  // =====================================================

  const loginSuccess = (jwt, user) =>
  {
    setToken(jwt);
    setCurrentUser(user);
    setIsLoggedIn(true);
  };


  // =====================================================
  // LOGOUT
  // =====================================================

  const logout = () =>
  {
    setToken(null);
    setCurrentUser(null);
    setIsLoggedIn(false);
    setFiles([]);
    setPage("login");
  };


  // =====================================================
  // LIST FILES
  // =====================================================

  const fetchFiles = () =>
  {
    fetch("http://localhost:8080/api/files", {
      headers: authHeaders()
    })
      .then((response) => response.text())
      .then((data) =>
      {
        if (data.trim() === "")
        {
          setFiles([]);
        }
        else
        {
          const fileList = data.trim().split(/\n+/);

          setFiles(fileList);
        }
      })
      .catch((error) =>
      {
        console.error(
          "Error fetching files:",
          error
        );
      });
  };


  // =====================================================
  // LOAD FILES AFTER LOGIN
  // =====================================================

  useEffect(() =>
  {
    if (isLoggedIn && token)
    {
      fetchFiles();
    }
  }, [isLoggedIn, token]);


  // =====================================================
  // DELETE FILE
  // =====================================================

  const deleteFile = (fileName) =>
  {
    fetch(
      `http://localhost:8080/api/files/${encodeURIComponent(fileName)}`,
      {
        method: "DELETE",
        headers: authHeaders()
      }
    )
      .then((response) => response.text())
      .then((message) =>
      {
        alert(message);

        // Get updated file list
        fetchFiles();
      })
      .catch((error) =>
      {
        console.error(
          "Error deleting file:",
          error
        );
      });
  };


  // =====================================================
  // RENAME FILE
  // =====================================================

  const renameFile = (oldName) =>
  {
    const newName = prompt(
      "Enter new file name:"
    );

    if (!newName || newName.trim() === "")
    {
      return;
    }

    fetch(
      `http://localhost:8080/api/files/rename?oldFileName=${encodeURIComponent(oldName)}&newFileName=${encodeURIComponent(newName)}`,
      {
        method: "PUT",
        headers: authHeaders()
      }
    )
      .then((response) => response.text())
      .then((message) =>
      {
        alert(message);

        // Get updated file list
        fetchFiles();
      })
      .catch((error) =>
      {
        console.error(
          "Error renaming file:",
          error
        );
      });
  };


  // =====================================================
  // DOWNLOAD FILE / GET
  // =====================================================

  const downloadFile = (fileName) =>
  {
    fetch(
      `http://localhost:8080/api/files/${encodeURIComponent(fileName)}/download`,
      {
        headers: authHeaders()
      }
    )
      .then((response) =>
      {
        // Errors (403/404) come back as plain text messages; a success
        // comes back as the raw file bytes - handle both.
        if (!response.ok)
        {
          return response.text().then((message) =>
          {
            throw new Error(message);
          });
        }

        return response.blob();
      })
      .then((blob) =>
      {
        // Trigger a real browser "Save As" download of the file bytes.
        const url = window.URL.createObjectURL(blob);

        const link = document.createElement("a");

        link.href = url;
        link.download = fileName;

        document.body.appendChild(link);
        link.click();
        link.remove();

        window.URL.revokeObjectURL(url);
      })
      .catch((error) =>
      {
        console.error(
          "Error downloading file:",
          error
        );

        alert(
          error.message || "Error downloading file"
        );
      });
  };


  // =====================================================
  // UPLOAD FILE / PUT
  // =====================================================

  const addFile = (selectedFile) =>
  {
    if (!selectedFile)
    {
      return;
    }

    const formData = new FormData();

    formData.append(
      "file",
      selectedFile
    );

    fetch(
      "http://localhost:8080/api/files/upload",
      {
        method: "POST",
        headers: authHeaders(),
        body: formData
      }
    )
      .then((response) => response.text())
      .then((message) =>
      {
        alert(message);

        // Get updated file list
        fetchFiles();
      })
      .catch((error) =>
      {
        console.error(
          "Error uploading file:",
          error
        );
      });
  };


  // =====================================================
  // FILE INFORMATION
  // =====================================================

  const showFileInfo = (fileName) =>
  {
    fetch(
      `http://localhost:8080/api/files/${encodeURIComponent(fileName)}/info`,
      {
        headers: authHeaders()
      }
    )
      .then((response) => response.text())
      .then((message) =>
      {
        alert(message);
      })
      .catch((error) =>
      {
        console.error(
          "Error getting file information:",
          error
        );
      });
  };


  // =====================================================
  // FILE SIZE
  // =====================================================

  const showFileSize = (fileName) =>
  {
    fetch(
      `http://localhost:8080/api/files/${encodeURIComponent(fileName)}/size`,
      {
        headers: authHeaders()
      }
    )
      .then((response) => response.text())
      .then((message) =>
      {
        alert(message);
      })
      .catch((error) =>
      {
        console.error(
          "Error getting file size:",
          error
        );
      });
  };


  // =====================================================
  // CHECK FILE EXISTS
  // =====================================================

  const checkFileExists = (fileName) =>
  {
    fetch(
      `http://localhost:8080/api/files/${encodeURIComponent(fileName)}/exists`,
      {
        headers: authHeaders()
      }
    )
      .then((response) => response.text())
      .then((message) =>
      {
        alert(message);
      })
      .catch((error) =>
      {
        console.error(
          "Error checking file:",
          error
        );
      });
  };


  // =====================================================
  // REGISTER PAGE
  // =====================================================

  if (!isLoggedIn && page === "register")
  {
    return (
      <Register
        goToLogin={() => setPage("login")}
      />
    );
  }


  // =====================================================
  // LOGIN PAGE
  // =====================================================

  if (!isLoggedIn)
  {
    return (
      <Login
        loginSuccess={loginSuccess}
        goToRegister={() => setPage("register")}
      />
    );
  }


  // =====================================================
  // FILEFLOW DASHBOARD
  // =====================================================

  return (
    <div>

      <Header user={currentUser} onLogout={logout} />

      <main>

        <FileUpload
          addFile={addFile}
        />

        <FileList
          files={files}
          deleteFile={deleteFile}
          downloadFile={downloadFile}
          renameFile={renameFile}
          showFileInfo={showFileInfo}
          showFileSize={showFileSize}
          checkFileExists={checkFileExists}
        />

      </main>

    </div>
  );
}

export default App;
