import { describe, it, expect, vi, beforeEach } from 'vitest'
import { getBookings, getAvailableRooms, createBooking, cancelBooking, confirmBooking } from './booking'
import request from '../utils/request'

vi.mock('../utils/request', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn()
  }
}))

describe('booking API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getBookings', () => {
    it('should_get_bookings_with_params_when_called_with_params', async () => {
      const params = { page: 1, size: 10 }
      const mockResponse = { code: 200, data: [], message: 'success' }
      request.get.mockResolvedValue(mockResponse)

      const result = await getBookings(params)

      expect(request.get).toHaveBeenCalledWith('/bookings', { params })
      expect(result).toEqual(mockResponse)
    })

    it('should_get_bookings_without_params_when_called_without_params', async () => {
      const mockResponse = { code: 200, data: [], message: 'success' }
      request.get.mockResolvedValue(mockResponse)

      const result = await getBookings()

      expect(request.get).toHaveBeenCalledWith('/bookings', { params: undefined })
      expect(result).toEqual(mockResponse)
    })
  })

  describe('getAvailableRooms', () => {
    it('should_get_available_rooms_when_called', async () => {
      const mockResponse = { code: 200, data: [], message: 'success' }
      request.get.mockResolvedValue(mockResponse)

      const result = await getAvailableRooms()

      expect(request.get).toHaveBeenCalledWith('/bookings/available-rooms')
      expect(result).toEqual(mockResponse)
    })
  })

  describe('createBooking', () => {
    it('should_create_booking_successfully_when_valid_data_provided', async () => {
      const bookingData = {
        roomId: 1,
        memberId: 1,
        checkInDate: '2026-05-10',
        checkOutDate: '2026-05-12',
        deposit: 200
      }
      const mockResponse = { code: 200, data: { id: 1 }, message: 'success' }
      request.post.mockResolvedValue(mockResponse)

      const result = await createBooking(bookingData)

      expect(request.post).toHaveBeenCalledWith('/bookings', bookingData)
      expect(result).toEqual(mockResponse)
    })

    it('should_throw_error_when_room_already_booked', async () => {
      const bookingData = {
        roomId: 1,
        memberId: 1,
        checkInDate: '2026-05-10',
        checkOutDate: '2026-05-12'
      }
      const mockError = new Error('客房当前不可预订')
      request.post.mockRejectedValue(mockError)

      await expect(createBooking(bookingData)).rejects.toThrow('客房当前不可预订')
      expect(request.post).toHaveBeenCalledWith('/bookings', bookingData)
    })

    it('should_throw_error_when_check_out_date_before_check_in_date', async () => {
      const bookingData = {
        roomId: 1,
        memberId: 1,
        checkInDate: '2026-05-12',
        checkOutDate: '2026-05-10'
      }
      const mockError = new Error('退房日期不能早于入住日期')
      request.post.mockRejectedValue(mockError)

      await expect(createBooking(bookingData)).rejects.toThrow('退房日期不能早于入住日期')
      expect(request.post).toHaveBeenCalledWith('/bookings', bookingData)
    })
  })

  describe('cancelBooking', () => {
    it('should_cancel_booking_successfully_when_valid_booking_id_provided', async () => {
      const bookingId = 1
      const mockResponse = { code: 200, data: { id: 1, status: '已取消' }, message: 'success' }
      request.put.mockResolvedValue(mockResponse)

      const result = await cancelBooking(bookingId)

      expect(request.put).toHaveBeenCalledWith('/bookings/1/cancel')
      expect(result).toEqual(mockResponse)
    })

    it('should_throw_error_when_cancelling_already_cancelled_booking', async () => {
      const bookingId = 1
      const mockError = new Error('预订已取消')
      request.put.mockRejectedValue(mockError)

      await expect(cancelBooking(bookingId)).rejects.toThrow('预订已取消')
      expect(request.put).toHaveBeenCalledWith('/bookings/1/cancel')
    })
  })

  describe('confirmBooking', () => {
    it('should_confirm_booking_successfully_when_valid_booking_id_provided', async () => {
      const bookingId = 1
      const mockResponse = { code: 200, data: { id: 1 }, message: 'success' }
      request.put.mockResolvedValue(mockResponse)

      const result = await confirmBooking(bookingId)

      expect(request.put).toHaveBeenCalledWith('/bookings/1/confirm')
      expect(result).toEqual(mockResponse)
    })
  })
})
