function Header({ user, onLogout })
{
  return (
    <header>
      <h1>📁 FileFlow</h1>
      <p>File Transfer & Management System</p>

      {user && (
        <div className="header-user">
          <span>Signed in as {user.name || user.email}</span>
          <button onClick={onLogout}>Logout</button>
        </div>
      )}
    </header>
  );
}

export default Header;
