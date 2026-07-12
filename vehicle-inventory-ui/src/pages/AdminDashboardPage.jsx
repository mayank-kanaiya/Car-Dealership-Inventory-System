import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, ShoppingCart, PackagePlus, Trash2, Edit } from 'lucide-react';
import {
  useVehicleList,
  useDeleteVehicle,
  usePurchase,
  useRestock,
} from '../features/vehicles/hooks/useVehicles';
import { PurchaseModal, RestockModal } from '../features/vehicles/components/InventoryModals';
import ConfirmDialog from '../components/ConfirmDialog/ConfirmDialog';
import Spinner from '../components/Spinner/Spinner';
import ErrorDisplay from '../components/ErrorDisplay/ErrorDisplay';
import Button from '../components/Button/Button';
import toast from 'react-hot-toast';

function AdminDashboardPage() {
  const navigate = useNavigate();
  const { data, isLoading, error } = useVehicleList({ page: 0, size: 50 });
  const deleteVehicle = useDeleteVehicle();
  const purchase = usePurchase();
  const restock = useRestock();

  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [modalType, setModalType] = useState(null);

  const handleDelete = async () => {
    try {
      await deleteVehicle.mutateAsync(selectedVehicle.id);
      toast.success('Vehicle deleted');
    } catch (err) {
      toast.error(err.message || 'Failed to delete');
    }
    setModalType(null);
    setSelectedVehicle(null);
  };

  const handlePurchase = async (id, quantity) => {
    try {
      await purchase.mutateAsync({ id, quantity });
      toast.success('Purchase successful');
    } catch (err) {
      toast.error(err.message || 'Failed to purchase');
    }
    setModalType(null);
    setSelectedVehicle(null);
  };

  const handleRestock = async (id, quantity) => {
    try {
      await restock.mutateAsync({ id, quantity });
      toast.success('Restock successful');
    } catch (err) {
      toast.error(err.message || 'Failed to restock');
    }
    setModalType(null);
    setSelectedVehicle(null);
  };

  if (isLoading) {
    return (
      <div className="flex justify-center py-20">
        <Spinner size="lg" />
      </div>
    );
  }

  if (error) {
    return <ErrorDisplay message={error.message} />;
  }

  const vehicles = data?.content || [];

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-text-primary">Admin Dashboard</h1>
        <Button variant="primary" onClick={() => navigate('/vehicles/new')}>
          <Plus size={16} className="mr-1" /> Add Vehicle
        </Button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {vehicles.map((vehicle) => (
          <div
            key={vehicle.id}
            className="bg-surface rounded-xl border border-border overflow-hidden"
          >
            <div className="aspect-video bg-gray-100 overflow-hidden">
              <img
                src={vehicle.imageUrl || '/images/default-vehicle.svg'}
                alt={`${vehicle.make} ${vehicle.model}`}
                className="w-full h-full object-cover"
                onError={(e) => {
                  e.target.src = '/images/default-vehicle.svg';
                }}
              />
            </div>
            <div className="p-4">
              <h3 className="text-lg font-semibold text-text-primary mb-1">
                {vehicle.make} {vehicle.model}
              </h3>
              <p className="text-sm text-text-secondary mb-1">
                {vehicle.category} - ${vehicle.price.toLocaleString()}
              </p>
              <p
                className={`text-sm font-medium mb-3 ${vehicle.quantityInStock > 0 ? 'text-success' : 'text-danger'}`}
              >
                {vehicle.quantityInStock > 0
                  ? `${vehicle.quantityInStock} in stock`
                  : 'Out of stock'}
              </p>
              <div className="flex gap-2 flex-wrap">
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => navigate(`/vehicles/${vehicle.id}/edit`)}
                >
                  <Edit size={14} /> Edit
                </Button>
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => {
                    setSelectedVehicle(vehicle);
                    setModalType('purchase');
                  }}
                >
                  <ShoppingCart size={14} /> Sell
                </Button>
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => {
                    setSelectedVehicle(vehicle);
                    setModalType('restock');
                  }}
                >
                  <PackagePlus size={14} /> Restock
                </Button>
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => {
                    setSelectedVehicle(vehicle);
                    setModalType('delete');
                  }}
                >
                  <Trash2 size={14} className="text-danger" />
                </Button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {selectedVehicle && modalType === 'purchase' && (
        <PurchaseModal
          vehicle={selectedVehicle}
          isOpen
          onClose={() => {
            setModalType(null);
            setSelectedVehicle(null);
          }}
          onConfirm={handlePurchase}
          isSubmitting={purchase.isPending}
        />
      )}
      {selectedVehicle && modalType === 'restock' && (
        <RestockModal
          vehicle={selectedVehicle}
          isOpen
          onClose={() => {
            setModalType(null);
            setSelectedVehicle(null);
          }}
          onConfirm={handleRestock}
          isSubmitting={restock.isPending}
        />
      )}
      {selectedVehicle && modalType === 'delete' && (
        <ConfirmDialog
          isOpen
          title="Delete Vehicle"
          message={`Are you sure you want to delete ${selectedVehicle.make} ${selectedVehicle.model}?`}
          onConfirm={handleDelete}
          onCancel={() => {
            setModalType(null);
            setSelectedVehicle(null);
          }}
        />
      )}
    </div>
  );
}

export default AdminDashboardPage;
