# Guía para Frontend del TPO – E-commerce (React + Vite)

> **Contexto:** Este documento le indica a **Claude Code** cómo construir el frontend de mi TPO de la materia *Aplicaciones Interactivas (UADE)*. El backend ya existe (Spring Boot + JWT, `SecurityConfig.java` ya configurado con CORS para `http://localhost:5173`). El frontend debe respetar el **estilo enseñado en clase**: nada de Next.js, nada de TypeScript, nada de Redux/Zustand/React Query. Solo lo que se vio: **Vite + React + JSX + Bootstrap + `useState` + `useEffect` + `fetch`**.

---

## PARTE 1 — Tareas a realizar (según el Trabajo Práctico Grupal)

### 1. Desarrollo de componentes

#### 1.1 Gestión de Usuarios
- [ ] Formulario de **Registro** (`Register.jsx`)
- [ ] Formulario de **Login** (`Login.jsx`)
- [ ] Página de **Perfil** del usuario (`Profile.jsx`)

#### 1.2 Catálogo de Productos
- [ ] Componente **`ProductList.jsx`** — lista los productos consumiendo el endpoint público `GET /api/productos`
- [ ] Componente **`ProductCard.jsx`** — card reutilizable para mostrar cada producto
- [ ] Componente **`ProductDetail.jsx`** — detalle de un producto individual

#### 1.3 Gestión de Productos (Panel Admin) — ABM
- [ ] Componente **`AdminProductList.jsx`** — listado con acciones de editar/eliminar (solo visible para ADMIN)
- [ ] Componente **`ProductForm.jsx`** — formulario reutilizable para Alta y Modificación
- [ ] Botón/acción de **Baja** con confirmación

### 2. Configuración de Seguridad y CORS
- [x] El backend ya tiene `SecurityConfig.java` con CORS habilitado para `http://localhost:5173` (puerto por defecto de Vite). **No hace falta tocar el backend.**
- [ ] El frontend debe enviar el **JWT** en el header `Authorization: Bearer <token>` en los endpoints protegidos (POST/PUT/DELETE de productos, `/api/pedidos/**`, `/api/admin/**`).

### 3. Integración con React Hooks (consumo de API)
- [ ] Usar **`useEffect`** con array de dependencias **`[]`** para la carga inicial de productos (al montar el componente).
- [ ] Usar **`useEffect`** con dependencia (ej: `[categoriaSeleccionada]`) cuando se aplique un filtro.
- [ ] Usar **`useState`** para manejar: lista de productos, estado de carga (`loading`), estado de error (`error`), y formularios.

### 4. Ciclo de prueba integral
1. Levantar la base de datos.
2. Levantar el backend Spring Boot (`http://localhost:8080`).
3. Levantar el frontend (`npm run dev` → `http://localhost:5173`).
4. Verificar que los componentes rendericen datos reales desde la BD.

### 5. Entrega
- [ ] Subir cambios al repositorio de GitHub.
- [ ] Grabar video (≤ 5 min) mostrando los componentes funcionando.
- [ ] Subir código + video al canal del grupo.

---

## PARTE 2 — Guía de implementación para Claude Code

> **Para Claude Code:** Seguí esta guía paso a paso. **No agregues librerías que no estén en la lista permitida.** El profesor evalúa que el código esté alineado a lo enseñado en clase.

### Restricciones de stack (IMPORTANTE)

**Permitido:**
- Vite (build tool)
- React (JavaScript, **NO TypeScript**)
- JSX
- Bootstrap (CSS framework — se mencionó explícitamente en clase)
- React Router DOM (para navegar entre páginas)
- `fetch` nativo del navegador (NO axios, salvo que el usuario lo pida)
- Hooks nativos: `useState`, `useEffect`, `useContext` (para auth)

**Prohibido (no se vio en clase):**
- TypeScript
- Next.js / Remix
- Tailwind, Chakra, Material UI, shadcn (la clase usa Bootstrap)
- Redux, Zustand, Recoil, Jotai
- React Query / SWR / TanStack Query
- Axios (salvo pedido explícito)
- Styled Components / Emotion

### Paso 1: Crear el proyecto

```bash
npm create vite@latest e-commerce-front -- --template react
cd e-commerce-front
npm install
```

> **Nota:** La clase usa el nombre `e-commerce-front`. Si el usuario ya tiene una carpeta, ajustar.

### Paso 2: Instalar dependencias mínimas

```bash
npm install bootstrap react-router-dom
```

En `src/main.jsx`, importar Bootstrap **una sola vez**:

```jsx
import 'bootstrap/dist/css/bootstrap.min.css'
```

### Paso 3: Estructura de carpetas

Mantenerla simple, al estilo de la clase (componentes en PascalCase):

```
src/
├── main.jsx
├── App.jsx
├── components/
│   ├── ProductCard.jsx
│   ├── ProductList.jsx
│   ├── ProductDetail.jsx
│   ├── ProductForm.jsx
│   ├── AdminProductList.jsx
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── Profile.jsx
│   └── Navbar.jsx
├── context/
│   └── AuthContext.jsx
└── services/
    └── api.js
```

### Paso 4: Configurar la URL del backend

Crear `src/services/api.js`:

```js
// URL base del backend Spring Boot
export const API_URL = 'http://localhost:8080/api';

// Helper para obtener el token guardado
export const getToken = () => localStorage.getItem('token');

// Helper para armar headers con JWT
export const authHeaders = () => {
  const token = getToken();
  return {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` }),
  };
};
```

> El `SecurityConfig.java` ya permite `http://localhost:5173` como origen, y acepta `Authorization` en headers (`setAllowedHeaders(List.of("*"))`).

### Paso 5: Contexto de autenticación

Crear `src/context/AuthContext.jsx`. Usar `useContext` + `useState` (esto sí es parte de hooks básicos de React):

```jsx
import { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });

  const login = (userData, token) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
```

### Paso 6: Componente `ProductList.jsx` (es el ejemplo que pide el TPO)

Este es **el componente clave** que el profesor menciona explícitamente. Debe seguir el patrón del PDF de la clase (CORS.pdf y useEffect.pdf):

```jsx
import { useState, useEffect } from 'react';
import { API_URL } from '../services/api';
import ProductCard from './ProductCard';

function ProductList() {
  // useState: estado para los productos, loading y error
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // useEffect con [] → se ejecuta solo una vez, al montar el componente
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await fetch(`${API_URL}/productos`);
        if (!response.ok) {
          throw new Error('Error al cargar los productos');
        }
        const data = await response.json();
        setProducts(data);
        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchProducts();
  }, []); // array vacío = solo una vez

  if (loading) return <p>Cargando productos...</p>;
  if (error) return <p className="text-danger">Error: {error}</p>;

  return (
    <div className="container mt-4">
      <h2>Catálogo de Productos</h2>
      <div className="row">
        {products.map((product) => (
          <div className="col-md-4 mb-3" key={product.id}>
            <ProductCard product={product} />
          </div>
        ))}
      </div>
    </div>
  );
}

export default ProductList;
```

### Paso 7: Componente `ProductCard.jsx` (recibe props, inmutables)

```jsx
function ProductCard({ product }) {
  return (
    <div className="card h-100">
      <div className="card-body">
        <h5 className="card-title">{product.nombre}</h5>
        <p className="card-text">{product.descripcion}</p>
        <p className="card-text"><strong>${product.precio}</strong></p>
        <p className="card-text"><small>Stock: {product.stock}</small></p>
      </div>
    </div>
  );
}

export default ProductCard;
```

### Paso 8: Componentes de autenticación

**`Login.jsx`** — usa `useState` para los inputs y `fetch` para llamar a `/api/auth/login`:

```jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { API_URL } from '../services/api';
import { useAuth } from '../context/AuthContext';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const response = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
      if (!response.ok) throw new Error('Credenciales inválidas');
      const data = await response.json();
      // Ajustar según la estructura que devuelva el backend
      login(data.user || { email }, data.token);
      navigate('/');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="container mt-4" style={{ maxWidth: '400px' }}>
      <h2>Iniciar Sesión</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Email</label>
          <input
            type="email"
            className="form-control"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Contraseña</label>
          <input
            type="password"
            className="form-control"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && <p className="text-danger">{error}</p>}
        <button type="submit" className="btn btn-primary">Ingresar</button>
      </form>
    </div>
  );
}

export default Login;
```

**`Register.jsx`** — mismo patrón, apunta a `/api/auth/register` (verificar el endpoint real del backend en el `AuthController`).

### Paso 9: Componentes Admin (ABM)

**`ProductForm.jsx`** — formulario reutilizable para alta y modificación. Recibe `product` como prop opcional (si viene, es edición):

```jsx
import { useState, useEffect } from 'react';
import { API_URL, authHeaders } from '../services/api';

function ProductForm({ product, onSaved }) {
  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [precio, setPrecio] = useState('');
  const [stock, setStock] = useState('');

  // useEffect con dependencia [product]: si cambia el producto a editar, repobla el form
  useEffect(() => {
    if (product) {
      setNombre(product.nombre || '');
      setDescripcion(product.descripcion || '');
      setPrecio(product.precio || '');
      setStock(product.stock || '');
    }
  }, [product]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const body = { nombre, descripcion, precio: Number(precio), stock: Number(stock) };
    const url = product
      ? `${API_URL}/productos/${product.id}`
      : `${API_URL}/productos`;
    const method = product ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method,
        headers: authHeaders(),
        body: JSON.stringify(body),
      });
      if (!response.ok) throw new Error('Error al guardar');
      const saved = await response.json();
      if (onSaved) onSaved(saved);
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="card p-3">
      <h3>{product ? 'Editar Producto' : 'Nuevo Producto'}</h3>
      <div className="mb-2">
        <label>Nombre</label>
        <input className="form-control" value={nombre} onChange={(e) => setNombre(e.target.value)} required />
      </div>
      <div className="mb-2">
        <label>Descripción</label>
        <textarea className="form-control" value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
      </div>
      <div className="mb-2">
        <label>Precio</label>
        <input type="number" className="form-control" value={precio} onChange={(e) => setPrecio(e.target.value)} required />
      </div>
      <div className="mb-2">
        <label>Stock</label>
        <input type="number" className="form-control" value={stock} onChange={(e) => setStock(e.target.value)} required />
      </div>
      <button type="submit" className="btn btn-success">Guardar</button>
    </form>
  );
}

export default ProductForm;
```

**`AdminProductList.jsx`** — lista con botones de editar/eliminar. La eliminación llama `DELETE /api/productos/{id}` con el JWT.

### Paso 10: `App.jsx` con rutas

```jsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import ProductList from './components/ProductList';
import Login from './components/Login';
import Register from './components/Register';
import Profile from './components/Profile';
import AdminProductList from './components/AdminProductList';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/" element={<ProductList />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/admin/productos" element={<AdminProductList />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
```

### Paso 11: Verificar endpoints del backend

Antes de codear, **mirar los `@Controller` del proyecto Spring Boot** para confirmar:
- Estructura exacta de los DTOs (¿el campo se llama `nombre` o `name`? ¿`precio` o `price`?).
- Endpoint exacto de login (`/api/auth/login` o `/api/auth/authenticate`).
- Qué devuelve login: ¿`{ token: "..." }` o `{ accessToken: "...", user: {...} }`?
- Estructura de roles para mostrar/ocultar el panel admin.

**Si los nombres de campos no coinciden, ajustar el JSX. No inventar nombres.**

### Paso 12: Checklist final antes de entregar

- [ ] `npm run dev` levanta sin errores en `http://localhost:5173`.
- [ ] El listado de productos carga datos reales desde el backend.
- [ ] Login guarda el token en localStorage.
- [ ] Endpoints protegidos envían el header `Authorization: Bearer <token>`.
- [ ] Panel admin solo se ve si el usuario tiene rol ADMIN.
- [ ] No hay warnings de ESLint sin atender (la clase mencionó ESLint).
- [ ] Los componentes están en **PascalCase** (`ProductList.jsx`, no `productList.jsx`).
- [ ] Las clases CSS usan `className` (no `class`).
- [ ] Bootstrap está importado una sola vez en `main.jsx`.

---

## Notas adicionales para Claude Code

1. **Si el usuario pregunta por algo que no está en la clase** (ej: "agregar Tailwind", "migrar a TypeScript"), avisarle que se sale del scope de la materia y preguntar antes de hacerlo.
2. **No agregar tests automatizados** (Jest, Vitest, Testing Library) salvo que se pidan — no se vieron en clase.
3. **Mantener los componentes funcionales, no de clase.** La clase enseñó solo componentes funcionales con hooks.
4. **El estado se maneja con `useState` en el componente** o con `useContext` para auth global. No usar Redux ni similares.
5. **Los errores se manejan con `try/catch`** dentro del `useEffect` o handler, y se muestran con un `useState` de error. Es el patrón que se vio en el PDF de CORS.
6. **Inline styles como objetos** están permitidos (la clase los mencionó), pero priorizar clases de Bootstrap.
