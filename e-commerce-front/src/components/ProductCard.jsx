import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { API_URL, authHeaders } from '../services/api';

function ProductCard({ product, onAddToCart }) {
  const { user } = useAuth();

  const handleAddToCart = async () => {
    if (!user) return;
    try {
      const response = await fetch(`${API_URL}/carrito/items`, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ productoId: product.id, cantidad: 1 }),
      });
      if (!response.ok) throw new Error('No se pudo agregar al carrito');
      if (onAddToCart) onAddToCart();
      alert('Producto agregado al carrito');
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="card h-100 shadow-sm">
      {product.imagenUrl && (
        <img src={product.imagenUrl} className="card-img-top" alt={product.nombre} style={{ height: '200px', objectFit: 'cover' }} />
      )}
      <div className="card-body d-flex flex-column">
        <h5 className="card-title">{product.nombre}</h5>
        {product.categoriaNombre && (
          <span className="badge bg-secondary mb-2" style={{ width: 'fit-content' }}>{product.categoriaNombre}</span>
        )}
        <p className="card-text text-muted" style={{ fontSize: '0.9rem' }}>{product.descripcion}</p>
        {product.talle && <p className="card-text mb-1"><small>Talle: {product.talle}</small></p>}
        {product.color && <p className="card-text mb-1"><small>Color: {product.color}</small></p>}
        <p className="card-text fw-bold fs-5 mt-auto">${product.precio?.toFixed(2)}</p>
        <p className="card-text"><small className={product.stock > 0 ? 'text-success' : 'text-danger'}>
          {product.stock > 0 ? `Stock: ${product.stock}` : 'Sin stock'}
        </small></p>
        <div className="d-flex gap-2 mt-2">
          <Link to={`/productos/${product.id}`} className="btn btn-outline-primary btn-sm flex-fill">
            Ver detalle
          </Link>
          {user && product.stock > 0 && (
            <button className="btn btn-primary btn-sm flex-fill" onClick={handleAddToCart}>
              Agregar al carrito
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default ProductCard;
