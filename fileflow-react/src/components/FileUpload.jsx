import { useState } from "react";

function FileUpload({ addFile })
{
  const [selectedFile, setSelectedFile] = useState(null);


  // =====================================================
  // SELECT FILE
  // =====================================================

  const handleFileChange = (event) =>
  {
    setSelectedFile(
      event.target.files[0]
    );
  };


  // =====================================================
  // UPLOAD FILE
  // =====================================================

  const handleUpload = () =>
  {
    if (!selectedFile)
    {
      alert("Please select a file first.");
      return;
    }


    // Send selected File object to App.jsx

    addFile(selectedFile);


    // Clear selected file

    setSelectedFile(null);
  };


  // =====================================================
  // USER INTERFACE
  // =====================================================

  return (
    <section className="upload-section">

      <h2>📤 Upload File</h2>


      <input
        type="file"
        onChange={handleFileChange}
      />


      <button
        onClick={handleUpload}
      >
        Upload File
      </button>


      {selectedFile && (
        <p>
          Selected File: {selectedFile.name}
        </p>
      )}


      <p>
        Select a file from your computer
        to upload it to the server.
      </p>

    </section>
  );
}

export default FileUpload;