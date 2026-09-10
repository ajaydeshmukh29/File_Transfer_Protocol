function FileList({
  files,
  deleteFile,
  downloadFile,
  renameFile,
  showFileInfo,
  showFileSize,
  checkFileExists
})
{
  // =====================================================
  // NO FILES
  // =====================================================

  if (files.length === 0)
  {
    return (
      <section className="file-list">

        <h2>My Files</h2>

        <p>No files available</p>

      </section>
    );
  }


  // =====================================================
  // FILE LIST
  // =====================================================

  return (
    <section className="file-list">

      <h2>My Files</h2>


      {files.map((file, index) => (

        <div
          className="file-item"
          key={index}
        >

          <h3>{file}</h3>


          <div className="file-buttons">


            {/* DOWNLOAD */}

            <button
              onClick={() =>
                downloadFile(file)
              }
            >
              Download
            </button>


            {/* RENAME */}

            <button
              onClick={() =>
                renameFile(file)
              }
            >
              Rename
            </button>


            {/* FILE INFORMATION */}

            <button
              onClick={() =>
                showFileInfo(file)
              }
            >
              Info
            </button>


            {/* FILE SIZE */}

            <button
              onClick={() =>
                showFileSize(file)
              }
            >
              Size
            </button>


            {/* CHECK FILE EXISTS */}

            <button
              onClick={() =>
                checkFileExists(file)
              }
            >
              Exists
            </button>


            {/* DELETE */}

            <button
              onClick={() =>
                deleteFile(file)
              }
            >
              Delete
            </button>


          </div>

        </div>

      ))}

    </section>
  );
}

export default FileList;