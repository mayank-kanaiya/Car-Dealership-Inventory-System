import { useState } from 'react';
import PropTypes from 'prop-types';
import Modal from '../../../components/Modal/Modal';
import Input from '../../../components/Input/Input';
import Button from '../../../components/Button/Button';

function PurchaseModal({ vehicle, isOpen, onClose, onConfirm, isSubmitting = false }) {
  const [quantity, setQuantity] = useState(1);
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const num = parseInt(quantity, 10);
    if (!num || num < 1) {
      setError('Quantity must be at least 1');
      return;
    }
    if (num > vehicle.quantityInStock) {
      setError(`Only ${vehicle.quantityInStock} units available`);
      return;
    }
    onConfirm(vehicle.id, num);
    setQuantity(1);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Purchase ${vehicle?.make} ${vehicle?.model}`}>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <p className="text-sm text-text-secondary">
          Available: <span className="font-semibold">{vehicle?.quantityInStock}</span> units
        </p>
        <Input
          label="Quantity"
          type="number"
          min="1"
          max={vehicle?.quantityInStock}
          value={quantity}
          onChange={(e) => setQuantity(e.target.value)}
          error={error}
        />
        <div className="flex gap-3 pt-2">
          <Button type="submit" variant="primary" disabled={isSubmitting}>
            {isSubmitting ? 'Processing...' : 'Confirm Purchase'}
          </Button>
          <Button type="button" variant="ghost" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
        </div>
      </form>
    </Modal>
  );
}

PurchaseModal.propTypes = {
  vehicle: PropTypes.shape({
    id: PropTypes.string.isRequired,
    make: PropTypes.string.isRequired,
    model: PropTypes.string.isRequired,
    quantityInStock: PropTypes.number.isRequired,
  }),
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onConfirm: PropTypes.func.isRequired,
  isSubmitting: PropTypes.bool,
};

function RestockModal({ vehicle, isOpen, onClose, onConfirm, isSubmitting = false }) {
  const [quantity, setQuantity] = useState(10);
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const num = parseInt(quantity, 10);
    if (!num || num < 1) {
      setError('Quantity must be at least 1');
      return;
    }
    onConfirm(vehicle.id, num);
    setQuantity(10);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Restock ${vehicle?.make} ${vehicle?.model}`}>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <p className="text-sm text-text-secondary">
          Current stock: <span className="font-semibold">{vehicle?.quantityInStock}</span> units
        </p>
        <Input
          label="Quantity to Add"
          type="number"
          min="1"
          value={quantity}
          onChange={(e) => setQuantity(e.target.value)}
          error={error}
        />
        <div className="flex gap-3 pt-2">
          <Button type="submit" variant="primary" disabled={isSubmitting}>
            {isSubmitting ? 'Processing...' : 'Confirm Restock'}
          </Button>
          <Button type="button" variant="ghost" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
        </div>
      </form>
    </Modal>
  );
}

RestockModal.propTypes = {
  vehicle: PropTypes.shape({
    id: PropTypes.string.isRequired,
    make: PropTypes.string.isRequired,
    model: PropTypes.string.isRequired,
    quantityInStock: PropTypes.number.isRequired,
  }),
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onConfirm: PropTypes.func.isRequired,
  isSubmitting: PropTypes.bool,
};

export { PurchaseModal, RestockModal };
